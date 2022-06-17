package com.jim.moviecritics.data.source.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.jim.moviecritics.data.Comment
import com.jim.moviecritics.data.HomeItem
import com.jim.moviecritics.data.PopularMoviesResult
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.source.ApplicationDataSource

class ApplicationLocalDataSource(val context: Context) : ApplicationDataSource {
    override suspend fun getPopularMovies(): Result<PopularMoviesResult> {
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
}