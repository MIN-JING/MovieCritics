package com.jim.moviecritics.data.source

import androidx.lifecycle.MutableLiveData
import com.jim.moviecritics.data.*


/**
 *  Interface to the Application layers.
 */
interface ApplicationRepository {

    // ApiDataSource
    suspend fun getPopularMovies(): Result<List<HomeItem>>

    suspend fun getMovieDetail(id: Int): Result<MovieDetailResult>

    suspend fun getMovieCredit(id: Int): Result<CreditResult>

    // FirebaseDataSource
    suspend fun getScores(imdbID: String): Result<List<Score>>

    suspend fun getScore(imdbID: String, userID: Long): Result<Score>

    suspend fun getUser(userID: Long): Result<User>

    suspend fun getComments(): Result<List<Comment>>

    fun getLiveComments(): MutableLiveData<List<Comment>>

    suspend fun comment(comment: Comment): Result<Boolean>

    suspend fun delete(comment: Comment): Result<Boolean>

    fun loadMockComment(): Comment

    fun loadMockScore(): Score

    suspend fun pushWatchedMovie(imdbID: String, userID: Long): Result<Boolean>

    suspend fun removeWatchedMovie(imdbID: String, userID: Long): Result<Boolean>

    suspend fun pushPopularMovies(pushTrend: PushTrend): Result<Boolean>
}