package com.jim.moviecritics.data.source

import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.jim.moviecritics.data.*

/**
 * Concrete implementation to load Application sources.
 */
class DefaultApplicationRepository(
    private val apiDataSource: ApplicationDataSource,
    private val firebaseDataSource: ApplicationDataSource,
    private val localDataSource: ApplicationDataSource
) : ApplicationRepository {

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
    override fun getLiveWatchList(imdbID: String, userID: String): MutableLiveData<Watch> {
        return firebaseDataSource.getLiveWatchList(imdbID, userID)
    }

    override fun getLiveWatchListByUser(userID: String): MutableLiveData<List<Watch>> {
        return firebaseDataSource.getLiveWatchListByUser(userID)
    }

    override suspend fun pushMultiWatchListExpiration(
        imdbID: String,
        userID: String,
        expiration: Timestamp
    ): Result<Boolean> {
        return firebaseDataSource.pushMultiWatchListExpiration(imdbID, userID, expiration)
    }

    override suspend fun pushSingleWatchListExpiration(watch: Watch): Result<Boolean> {
        return firebaseDataSource.pushSingleWatchListExpiration(watch)
    }

    override suspend fun getScores(imdbID: String): Result<List<Score>> {
        return firebaseDataSource.getScores(imdbID)
    }

    override suspend fun getScore(imdbID: String, userID: String): Result<Score> {
        return firebaseDataSource.getScore(imdbID, userID)
    }

    override fun getLiveScore(imdbID: String, userID: String): MutableLiveData<Score> {
        return firebaseDataSource.getLiveScore(imdbID, userID)
    }

    override suspend fun pushUserInfo(user: User): Result<Boolean> {
        return firebaseDataSource.pushUserInfo(user)
    }

    override suspend fun getUserByToken(token: String): Result<User> {
        return firebaseDataSource.getUserByToken(token)
    }

    override suspend fun getUserById(id: String): Result<User?> {
        return firebaseDataSource.getUserById(id)
    }

    override suspend fun getUsersByIdList(idList: List<String>): Result<List<User>> {
        return firebaseDataSource.getUsersByIdList(idList)
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

    override fun getLiveCommentsExcludeBlocks(imdbID: String, blocks: List<String>): MutableLiveData<List<Comment>> {
        return firebaseDataSource.getLiveCommentsExcludeBlocks(imdbID, blocks)
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

    override suspend fun pushWatchlistMovie(watch: Watch): Result<Boolean> {
        return firebaseDataSource.pushWatchlistMovie(watch)
    }

    override suspend fun removeWatchlistMovie(imdbID: String, userID: String): Result<Boolean> {
        return firebaseDataSource.removeWatchlistMovie(imdbID, userID)
    }

    override suspend fun pushScore(score: Score): Result<Boolean> {
        return firebaseDataSource.pushScore(score)
    }

    override suspend fun pushReport(report: Report): Result<Boolean> {
        return firebaseDataSource.pushReport(report)
    }

    override suspend fun pushBlockUser(userID: String, blockedID: String): Result<Boolean> {
        return firebaseDataSource.pushBlockUser(userID, blockedID)
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