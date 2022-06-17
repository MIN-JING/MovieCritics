package com.jim.moviecritics.data.source.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jim.moviecritics.MovieApplication
import com.jim.moviecritics.R
import com.jim.moviecritics.data.Comment
import com.jim.moviecritics.data.source.ApplicationDataSource
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.jim.moviecritics.data.Result
import com.google.firebase.Timestamp
import com.jim.moviecritics.data.HomeItem
import com.jim.moviecritics.data.PopularMoviesResult

/**
 * Implementation of the Application source that from network.
 */
object FirebaseDataSource : ApplicationDataSource {

    private const val PATH_COMMENTS = "comment"
    private const val KEY_CREATED_TIME = "createdTime"

    override suspend fun getPopularMovies(): Result<PopularMoviesResult> {
        TODO("Not yet implemented")
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
                        Log.d("Jim", "${document.id} => ${document.data}")

                        val comment = document.toObject(Comment::class.java)
                        list.add(comment)
                    }
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let {
                        Log.d("Jim", "${this::class.simpleName} Error getting documents. message = ${it.message}")
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
                Log.i("Jim", "ApplicationRemoteDataSource addSnapshotListener detect")

                exception?.let {
                    Log.w("Jim", "${this::class.simpleName} Error getting documents. message = ${it.message}")
                }

                val list = mutableListOf<Comment>()
                if (snapshot != null) {
                    snapshot.forEach { document ->
                        Log.d("Jim", "${document.id} => ${document.data}")

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
                    Log.i("Jim", "Comment: $comment")

                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {
                        Log.w("Jim", "${this::class.simpleName} Error getting documents. message = ${it.message}")
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
                        Log.i("Jim", "Delete: $comment")

                        continuation.resume(Result.Success(true))
                    }.addOnFailureListener {
                        Log.w("Jim", "${this::class.simpleName} Error getting documents. message = ${it.message}")
                        continuation.resume(Result.Error(it))
                    }
            }
        }
    }

    override suspend fun loadMockDataComment(): Result<Comment> {
        TODO("Not yet implemented")
    }
}