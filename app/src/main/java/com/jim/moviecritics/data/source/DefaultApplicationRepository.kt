package com.jim.moviecritics.data.source

import androidx.lifecycle.MutableLiveData
import com.jim.moviecritics.data.*


/**
 * Concrete implementation to load Application sources.
 */
class DefaultApplicationRepository(
    private val apiDataSource: ApplicationDataSource,
    private val firebaseDataSource: ApplicationDataSource,
    private val localDataSource: ApplicationDataSource ) : ApplicationRepository {


    // ApiDataSource
    override suspend fun getPopularMovies(): Result<List<HomeItem>> {
        return apiDataSource.getPopularMovies()
    }

    override suspend fun getMovieDetail(id: Int): Result<MovieDetailResult> {
        return apiDataSource.getMovieDetail(id)
    }

    override suspend fun getMovieCredit(id: Int): Result<CreditResult> {
        return apiDataSource.getMovieCredit(id)
    }

    override suspend fun getSearchMulti(queryKey: String): Result<List<LookItem>> {
        return apiDataSource.getSearchMulti(queryKey)
    }

    // FirebaseDataSource
    override suspend fun getScores(imdbID: String): Result<List<Score>> {
        return firebaseDataSource.getScores(imdbID)
    }

    override suspend fun getScore(imdbID: String, userID: Long): Result<Score> {
        return firebaseDataSource.getScore(imdbID, userID)
    }

    override fun getLiveScore(imdbID: String, userID: Long): MutableLiveData<Score> {
        return firebaseDataSource.getLiveScore(imdbID, userID)
    }

    override suspend fun getUser(userID: Long): Result<User> {
        return firebaseDataSource.getUser(userID)
    }

    override suspend fun getComments(): Result<List<Comment>> {
        return firebaseDataSource.getComments()
    }

    override fun getLiveComments(): MutableLiveData<List<Comment>> {
        return firebaseDataSource.getLiveComments()
    }

    override suspend fun comment(comment: Comment): Result<Boolean> {
        return firebaseDataSource.comment(comment)
    }

    override suspend fun delete(comment: Comment): Result<Boolean> {
        return firebaseDataSource.delete(comment)
    }

    override suspend fun pushWatchedMovie(imdbID: String, userID: Long): Result<Boolean> {
        return firebaseDataSource.pushWatchedMovie(imdbID, userID)
    }

    override suspend fun removeWatchedMovie(imdbID: String, userID: Long): Result<Boolean> {
        return firebaseDataSource.removeWatchedMovie(imdbID, userID)
    }

    override suspend fun pushLikedMovie(imdbID: String, userID: Long): Result<Boolean> {
        return firebaseDataSource.pushLikedMovie(imdbID, userID)
    }

    override suspend fun removeLikedMovie(imdbID: String, userID: Long): Result<Boolean> {
        return firebaseDataSource.removeLikedMovie(imdbID, userID)
    }

    override suspend fun pushWatchlistMovie(imdbID: String, userID: Long): Result<Boolean> {
        return firebaseDataSource.pushWatchlistMovie(imdbID, userID)
    }

    override suspend fun removeWatchlistMovie(imdbID: String, userID: Long): Result<Boolean> {
        return firebaseDataSource.removeWatchlistMovie(imdbID, userID)
    }

    override suspend fun pushScore(score: Score): Result<Boolean> {
        return firebaseDataSource.pushScore(score)
    }

    override suspend fun pushPopularMovies(pushTrend: PushTrend): Result<Boolean> {
        return firebaseDataSource.pushPopularMovies(pushTrend)
    }

    override suspend fun pushMockComment(): Result<Boolean> {
        return localDataSource.pushMockComment()
    }

    override suspend fun pushMockScore(): Result<Boolean> {
        return localDataSource.pushMockScore()
    }

    override suspend fun pushMockUser(): Result<Boolean> {
        return localDataSource.pushMockUser()
    }
}