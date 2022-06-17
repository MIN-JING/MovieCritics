package com.jim.moviecritics.data.source

import androidx.lifecycle.MutableLiveData
import com.jim.moviecritics.data.Comment
import com.jim.moviecritics.data.HomeItem
import com.jim.moviecritics.data.PopularMoviesResult
import com.jim.moviecritics.data.Result


/**
 *  Interface to the Application layers.
 */
interface ApplicationRepository {

    // ApiDataSource
    suspend fun getPopularMovies(): Result<PopularMoviesResult>

    // FirebaseDataSource
    suspend fun getComments(): Result<List<Comment>>

    fun getLiveComments(): MutableLiveData<List<Comment>>

    suspend fun comment(comment: Comment): Result<Boolean>

    suspend fun delete(comment: Comment): Result<Boolean>
}