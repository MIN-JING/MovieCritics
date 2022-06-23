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

    // FirebaseDataSource
    override suspend fun getScores(imdbID: String): Result<List<Score>> {
        return firebaseDataSource.getScores(imdbID)
    }

    override suspend fun getScore(imdbID: String, userID: Long): Result<Score> {
        return firebaseDataSource.getScore(imdbID, userID)
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

    override fun loadMockComment(): Comment {
        return localDataSource.loadMockComment()
    }

    override fun loadMockScore(): Score {
        return localDataSource.loadMockScore()
    }

    override suspend fun pushWatchedMovie(imdbID: String, userID: Long): Result<Boolean> {
        return firebaseDataSource.pushWatchedMovie(imdbID, userID)
    }

    override suspend fun pushPopularMovies(pushTrend: PushTrend): Result<Boolean> {
        return firebaseDataSource.pushPopularMovies(pushTrend)
    }

}