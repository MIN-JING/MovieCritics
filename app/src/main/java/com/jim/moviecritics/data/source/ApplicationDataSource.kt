package com.jim.moviecritics.data.source

import androidx.lifecycle.MutableLiveData
import com.jim.moviecritics.data.*


/**
 * Main entry point for accessing Application sources.
 */
interface ApplicationDataSource {

    // ApiDataSource
    suspend fun getPopularMovies(): Result<List<HomeItem>>

    suspend fun getMovieDetail(id: Int): Result<MovieDetailResult>

    suspend fun getMovieCredit(id: Int): Result<CreditResult>

    suspend fun getSearchMulti(queryKey: String): Result<List<LookItem>>

    // FirebaseDataSource
    suspend fun getScores(imdbID: String): Result<List<Score>>

    suspend fun getScore(imdbID: String, userID: String): Result<Score>

    fun getLiveScore(imdbID: String, userID: String): MutableLiveData<Score>

    suspend fun userSignIn(user: User): Result<Boolean>

    suspend fun getUser(token: String): Result<User>

    suspend fun getComments(imdbID: String): Result<List<Comment>>

    fun getLiveComments(imdbID: String): MutableLiveData<List<Comment>>

    fun getLivePersonalComments(userID: String): MutableLiveData<List<Comment>>

    suspend fun pushComment(comment: Comment): Result<Boolean>

    suspend fun delete(comment: Comment): Result<Boolean>

    suspend fun pushWatchedMovie(imdbID: String, userID: String): Result<Boolean>

    suspend fun removeWatchedMovie(imdbID: String, userID: String): Result<Boolean>

    suspend fun pushLikedMovie(imdbID: String, userID: String): Result<Boolean>

    suspend fun removeLikedMovie(imdbID: String, userID: String): Result<Boolean>

    suspend fun pushWatchlistMovie(imdbID: String, userID: String): Result<Boolean>

    suspend fun removeWatchlistMovie(imdbID: String, userID: String): Result<Boolean>

    suspend fun pushScore(score: Score): Result<Boolean>

    suspend fun pushPopularMovies(trends: List<Trend>): Result<Boolean>

    suspend fun pushMockComment(): Result<Boolean>

    suspend fun pushMockScore(): Result<Boolean>

    suspend fun pushMockUser(): Result<Boolean>
}