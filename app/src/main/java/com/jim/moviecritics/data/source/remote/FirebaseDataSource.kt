package com.jim.moviecritics.data.source.remote


import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jim.moviecritics.MovieApplication
import com.jim.moviecritics.R
import com.jim.moviecritics.data.source.ApplicationDataSource
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.google.firebase.Timestamp
import com.jim.moviecritics.data.*
import com.jim.moviecritics.util.Logger

/**
 * Implementation of the Application source that from network.
 */
object FirebaseDataSource : ApplicationDataSource {

    private const val PATH_SCORES = "score"
    private const val PATH_COMMENTS = "comment"
    private const val PATH_POPULAR_MOVIES = "popularMovies"
    private const val KEY_CREATED_TIME = "createdTime"

    override suspend fun getPopularMovies(): Result<List<HomeItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMovieDetail(id: Int): Result<MovieDetailResult> {
        TODO("Not yet implemented")
    }


    override suspend fun getScore(imdbID: String): Result<List<Score>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_SCORES)
            .whereEqualTo("imdbID", imdbID)
            .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
//                    Logger.d( task.result.documents.first().id + " => " + task.result.documents.first().data)
//                    val item = task.result.documents.first().toObject(Score::class.java)
                    val list = mutableListOf<Score>()
                    for (document in task.result) {
                        Logger.d( document.id + " => " + document.data)
                        val score = document.toObject(Score::class.java)
                        list.add(score)
                    }
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(MovieApplication.instance.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun getComments(): Result<List<Comment>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_COMMENTS)
            .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Comment>()
                    for (document in task.result) {
                        Logger.d( document.id + " => " + document.data)

                        val comment = document.toObject(Comment::class.java)
                        list.add(comment)
                    }
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(MovieApplication.instance.getString(R.string.you_know_nothing)))
                }
            }
    }

    override fun getLiveComments(): MutableLiveData<List<Comment>> {
        val liveData = MutableLiveData<List<Comment>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_COMMENTS)
            .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                Logger.i("ApplicationRemoteDataSource addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                val list = mutableListOf<Comment>()
                if (snapshot != null) {
                    snapshot.forEach { document ->
                        Logger.d(document.id + " => " + document.data)

                        val comment = document.toObject(Comment::class.java)
                        list.add(comment)
                    }
                    liveData.value = list
                }
            }
        return liveData
    }

    override suspend fun comment(comment: Comment): Result<Boolean> = suspendCoroutine { continuation ->
        val comments = FirebaseFirestore.getInstance().collection(PATH_COMMENTS)
        val document = comments.document()

        comment.id = document.id
        comment.createdTime = Timestamp.now()

        document
            .set(comment)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("Comment: $comment")

                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(MovieApplication.instance.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun delete(comment: Comment): Result<Boolean> = suspendCoroutine { continuation ->

        when (comment.userID) {
            12344L -> {
                continuation.resume(Result.Fail("You know nothing!! ${comment.userID}"))
            }
            else -> {
                FirebaseFirestore.getInstance()
                    .collection(PATH_COMMENTS)
                    .document(comment.id)
                    .delete()
                    .addOnSuccessListener {
                        Logger.i("Delete: $comment")

                        continuation.resume(Result.Success(true))
                    }.addOnFailureListener {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                    }
            }
        }
    }

    override fun loadMockComment(): Comment {
        TODO("Not yet implemented")
    }

    override fun loadMockScore(): Score {
        TODO("Not yet implemented")
    }

    override suspend fun pushPopularMovies(pushTrend: PushTrend): Result<Boolean>  = suspendCoroutine { continuation ->
        val popularMovies = FirebaseFirestore.getInstance().collection(PATH_POPULAR_MOVIES)
        val document = popularMovies.document()

        pushTrend.documentID = document.id


        document
            .set(popularMovies)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("popularMovies: $popularMovies")

                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(MovieApplication.instance.getString(R.string.you_know_nothing)))
                }
            }

    }
}