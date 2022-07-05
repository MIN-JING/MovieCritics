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

    override suspend fun getFind(imdbID: String): Result<FindResult> {
        return apiDataSource.getFind(imdbID)
    }

    // FirebaseDataSource
    override suspend fun getScores(imdbID: String): Result<List<Score>> {
        return firebaseDataSource.getScores(imdbID)
    }

    override suspend fun getScore(imdbID: String, userID: String): Result<Score> {
        return firebaseDataSource.getScore(imdbID, userID)
    }

    override fun getLiveScore(imdbID: String, userID: String): MutableLiveData<Score> {
        return firebaseDataSource.getLiveScore(imdbID, userID)
    }

    override suspend fun userSignIn(user: User): Result<Boolean> {
        return firebaseDataSource.userSignIn(user)
    }

    override suspend fun getUser(token: String): Result<User> {
        return firebaseDataSource.getUser(token)
    }

    override suspend fun getComments(imdbID: String): Result<List<Comment>> {
        return firebaseDataSource.getComments(imdbID)
    }

    override fun getLiveComments(imdbID: String): MutableLiveData<List<Comment>> {
        return firebaseDataSource.getLiveComments(imdbID)
    }

    override fun getLivePersonalComments(userID: String): MutableLiveData<List<Comment>> {
        return firebaseDataSource.getLivePersonalComments(userID)
    }

    override suspend fun pushComment(comment: Comment): Result<Boolean> {
        return firebaseDataSource.pushComment(comment)
    }

    override suspend fun delete(comment: Comment): Result<Boolean> {
        return firebaseDataSource.delete(comment)
    }

    override fun getLivePersonalFavorites(userID: String): MutableLiveData<List<String>> {
        return firebaseDataSource.getLivePersonalFavorites(userID)
    }

    override suspend fun pushWatchedMovie(imdbID: String, userID: String): Result<Boolean> {
        return firebaseDataSource.pushWatchedMovie(imdbID, userID)
    }

    override suspend fun removeWatchedMovie(imdbID: String, userID: String): Result<Boolean> {
        return firebaseDataSource.removeWatchedMovie(imdbID, userID)
    }

    override suspend fun pushLikedMovie(imdbID: String, userID: String): Result<Boolean> {
        return firebaseDataSource.pushLikedMovie(imdbID, userID)
    }

    override suspend fun removeLikedMovie(imdbID: String, userID: String): Result<Boolean> {
        return firebaseDataSource.removeLikedMovie(imdbID, userID)
    }

    override suspend fun pushWatchlistMovie(imdbID: String, userID: String): Result<Boolean> {
        return firebaseDataSource.pushWatchlistMovie(imdbID, userID)
    }

    override suspend fun removeWatchlistMovie(imdbID: String, userID: String): Result<Boolean> {
        return firebaseDataSource.removeWatchlistMovie(imdbID, userID)
    }

    override suspend fun pushScore(score: Score): Result<Boolean> {
        return firebaseDataSource.pushScore(score)
    }

    override suspend fun pushPopularMovies(trends: List<Trend>): Result<Boolean> {
        return firebaseDataSource.pushPopularMovies(trends)
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