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

    suspend fun getSearchMulti(queryKey: String): Result<List<LookItem>>

    // FirebaseDataSource
    suspend fun getScores(imdbID: String): Result<List<Score>>

    suspend fun getScore(imdbID: String, userID: Long): Result<Score>

    fun getLiveScore(imdbID: String, userID: Long): MutableLiveData<Score>

    suspend fun getUser(userID: Long): Result<User>

    suspend fun getComments(): Result<List<Comment>>

    fun getLiveComments(): MutableLiveData<List<Comment>>

    suspend fun pushComment(comment: Comment): Result<Boolean>

    suspend fun delete(comment: Comment): Result<Boolean>

    suspend fun pushWatchedMovie(imdbID: String, userID: Long): Result<Boolean>

    suspend fun removeWatchedMovie(imdbID: String, userID: Long): Result<Boolean>

    suspend fun pushLikedMovie(imdbID: String, userID: Long): Result<Boolean>

    suspend fun removeLikedMovie(imdbID: String, userID: Long): Result<Boolean>

    suspend fun pushWatchlistMovie(imdbID: String, userID: Long): Result<Boolean>

    suspend fun removeWatchlistMovie(imdbID: String, userID: Long): Result<Boolean>

    suspend fun pushScore(score: Score): Result<Boolean>

    suspend fun pushPopularMovies(trends: List<Trend>): Result<Boolean>

    suspend fun pushMockComment(): Result<Boolean>

    suspend fun pushMockScore(): Result<Boolean>

    suspend fun pushMockUser(): Result<Boolean>

}