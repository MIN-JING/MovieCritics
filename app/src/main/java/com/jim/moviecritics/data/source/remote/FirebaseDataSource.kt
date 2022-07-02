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
import com.google.firebase.firestore.FieldValue
import com.jim.moviecritics.data.*
import com.jim.moviecritics.util.Logger


/**
 * Implementation of the Application source that from network.
 */
object FirebaseDataSource : ApplicationDataSource {

    private const val PATH_SCORES = "scores"
    private const val PATH_COMMENTS = "comments"
    private const val PATH_POPULAR_MOVIES = "popularMovies"
    private const val PATH_USERS = "users"
    private const val KEY_WATCHED = "watched"
    private const val KEY_LIKED = "liked"
    private const val KEY_WATCHLIST = "watchlist"
    private const val KEY_CREATED_TIME = "createdTime"


    override suspend fun getPopularMovies(): Result<List<HomeItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMovieDetail(id: Int): Result<MovieDetailResult> {
        TODO("Not yet implemented")
    }

    override suspend fun getMovieCredit(id: Int): Result<CreditResult> {
        TODO("Not yet implemented")
    }

    override suspend fun getSearchMulti(queryKey: String): Result<List<LookItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun getScores(imdbID: String): Result<List<Score>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_SCORES)
            .whereEqualTo("imdbID", imdbID)
            .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
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

    override suspend fun getScore(imdbID: String, userID: Long): Result<Score> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_SCORES)
            .whereEqualTo("imdbID", imdbID)
            .whereEqualTo("userID", userID)
            .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result.size() >= 1) {
                        Logger.w("[${this::class.simpleName}] getScore task.result.size >= 1")
                        Logger.d( task.result.first().id + " => " + task.result.first().data)
                        val item = task.result.first().toObject(Score::class.java)
                        continuation.resume(Result.Success(item))
                    } else {
                        Logger.w("[${this::class.simpleName}] getScore task.result.size < 1")
                        val item = Score(imdbID = imdbID, userID = userID)
                        continuation.resume(Result.Success(item))
                    }
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

    override fun getLiveScore(imdbID: String, userID: Long): MutableLiveData<Score> {
        val liveData = MutableLiveData<Score>()

        FirebaseFirestore.getInstance()
            .collection(PATH_SCORES)
            .whereEqualTo("imdbID", imdbID)
            .whereEqualTo("userID", userID)
            .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                Logger.i("getLiveScore addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                val list = mutableListOf<Score>()
                if (snapshot != null) {
                    snapshot.forEach { document ->
                        Logger.d(document.id + " => " + document.data)

                        val comment = document.toObject(Score::class.java)
                        list.add(comment)
                    }
                    Logger.d( list.first().id + " => " + list.first())
                    liveData.value = list.first()
                } else {
                    Logger.w("[${this::class.simpleName}] getLiveScore snapshot == null")
                }
            }
        return liveData
    }

    override suspend fun getUser(userID: Long): Result<User> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .whereEqualTo("id", userID)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result.size() >= 1) {
                        Logger.w("[${this::class.simpleName}] getUser task.result.size >= 1")
                        Logger.d( task.result.first().id + " => " + task.result.first().data)
                        val item = task.result.first().toObject(User::class.java)
                        continuation.resume(Result.Success(item))
                    } else {
                        Logger.w("[${this::class.simpleName}] getUser task.result.size < 1")
                        val item = User(id = userID)
                        continuation.resume(Result.Success(item))
                    }
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

//    override suspend fun pushUser()

    override suspend fun getComments(imdbID: String): Result<List<Comment>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_COMMENTS)
            .whereEqualTo("imdbID", imdbID)
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

    override fun getLiveComments(imdbID: String): MutableLiveData<List<Comment>> {
        val liveData = MutableLiveData<List<Comment>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_COMMENTS)
            .whereEqualTo("imdbID", imdbID)
            .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                Logger.i("getLiveComments addSnapshotListener detect")

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

    override suspend fun pushComment(comment: Comment): Result<Boolean> = suspendCoroutine { continuation ->
        val comments = FirebaseFirestore.getInstance().collection(PATH_COMMENTS)
        val document = comments.document()

        comment.id = document.id
        comment.createdTime = Timestamp.now()

        document
            .set(comment)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
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



    override suspend fun pushWatchedMovie(imdbID: String, userID: Long): Result<Boolean> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(userID.toString())
//            .set(watched, SetOptions.merge())
            .update(KEY_WATCHED, FieldValue.arrayUnion(imdbID))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("pushWatchedMovie task.isSuccessful")
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

    override suspend fun removeWatchedMovie(imdbID: String, userID: Long): Result<Boolean> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(userID.toString())
            .update(KEY_WATCHED, FieldValue.arrayRemove(imdbID))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("removeWatchedMovie task.isSuccessful")
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

    override suspend fun pushLikedMovie(imdbID: String, userID: Long): Result<Boolean> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(userID.toString())
            .update(KEY_LIKED, FieldValue.arrayUnion(imdbID))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("pushLikedMovie task.isSuccessful")
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

    override suspend fun removeLikedMovie(imdbID: String, userID: Long): Result<Boolean> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(userID.toString())
            .update(KEY_LIKED, FieldValue.arrayRemove(imdbID))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("removeLikedMovie task.isSuccessful")
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

    override suspend fun pushWatchlistMovie(imdbID: String, userID: Long): Result<Boolean> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(userID.toString())
            .update(KEY_WATCHLIST, FieldValue.arrayUnion(imdbID))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("pushWatchlistMovie task.isSuccessful")
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

    override suspend fun removeWatchlistMovie(imdbID: String, userID: Long): Result<Boolean> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(userID.toString())
            .update(KEY_WATCHLIST, FieldValue.arrayRemove(imdbID))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("removeWatchlistMovie task.isSuccessful")
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

    override suspend fun pushScore(score: Score): Result<Boolean>  = suspendCoroutine { continuation ->
        val scores = FirebaseFirestore.getInstance().collection(PATH_SCORES)
        val document = scores.document()

        score.id = document.id

        document
            .set(score)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("pushScore task.isSuccessful")
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

    override suspend fun pushPopularMovies(trends: List<Trend>): Result<Boolean>  = suspendCoroutine { continuation ->
        val popularMovies = FirebaseFirestore.getInstance().collection(PATH_POPULAR_MOVIES)

        for (trend in trends) {
            popularMovies
                .document(trend.id.toString())
                .set(trend)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Logger.i("pushPopularMovies task.isSuccessful")
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

    override suspend fun pushMockComment(): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushMockScore(): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushMockUser(): Result<Boolean> {
        TODO("Not yet implemented")
    }
}