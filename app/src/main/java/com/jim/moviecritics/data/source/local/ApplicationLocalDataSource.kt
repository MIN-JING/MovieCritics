package com.jim.moviecritics.data.source.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.jim.moviecritics.data.*
import com.jim.moviecritics.data.source.ApplicationDataSource

class ApplicationLocalDataSource(val context: Context) : ApplicationDataSource {
    override suspend fun getPopularMovies(): Result<List<HomeItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun getComments(): Result<List<Comment>> {
        TODO("Not yet implemented")
    }

    override fun getLiveComments(): MutableLiveData<List<Comment>> {
        TODO("Not yet implemented")
    }

    override suspend fun comment(comment: Comment): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(comment: Comment): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun loadMockDataComment(): Result<Comment> {
//        val comments = FirebaseFirestore.getInstance().collection("comment")
//        val document = comments.document()
        val data = Comment(
            "23456",
            12344,
            "tt0343818",
            Timestamp.now(),
            "Would Google LaMDA chat robot become a human?",
            listOf(12345, 45678, 89012),
            listOf(12345, 45678, 89012)
        )
//        document.set(data)
        return Result.Success(data)
    }

    override suspend fun pushPopularMovies(pushTrend: PushTrend): Result<Boolean> {
        TODO("Not yet implemented")
    }
}