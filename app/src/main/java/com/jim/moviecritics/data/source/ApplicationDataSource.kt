package com.jim.moviecritics.data.source

import androidx.lifecycle.MutableLiveData
import com.jim.moviecritics.data.*


/**
 * Main entry point for accessing Application sources.
 */
interface ApplicationDataSource {

    // ApiDataSource
    suspend fun getPopularMovies(): Result<List<HomeItem>>

    suspend fun getMoviesDetail(id: Int): Result<MoviesDetailResult>

    // FirebaseDataSource
    suspend fun getComments(): Result<List<Comment>>

    fun getLiveComments(): MutableLiveData<List<Comment>>

    suspend fun comment(comment: Comment): Result<Boolean>

    suspend fun delete(comment: Comment): Result<Boolean>

    suspend fun loadMockDataComment(): Result<Comment>

    suspend fun pushPopularMovies(pushTrend: PushTrend): Result<Boolean>
}