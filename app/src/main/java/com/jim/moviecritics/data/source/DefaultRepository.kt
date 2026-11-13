package com.jim.moviecritics.data.source

import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.jim.moviecritics.data.Comment
import com.jim.moviecritics.data.CreditResult
import com.jim.moviecritics.data.FindResult
import com.jim.moviecritics.data.HomeItem
import com.jim.moviecritics.data.LookItem
import com.jim.moviecritics.data.MovieDetailResult
import com.jim.moviecritics.data.Report
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.Score
import com.jim.moviecritics.data.Trend
import com.jim.moviecritics.data.User
import com.jim.moviecritics.data.Watch

/**
 * Concrete implementation to load Application sources.
 */
class DefaultRepository(
    private val apiDataSource: DataSource,
    private val firebaseDataSource: DataSource,
    private val localDataSource: DataSource
) : Repository {

    // ApiDataSource
    override suspend fun getPopularMovies(): Result<List<HomeItem>> =
        apiDataSource.getPopularMovies()

    override suspend fun getMovieDetail(id: Int): Result<MovieDetailResult> =
        apiDataSource.getMovieDetail(id)

    override suspend fun getMovieCredit(id: Int): Result<CreditResult> =
        apiDataSource.getMovieCredit(id)

    override suspend fun getSearchMulti(queryKey: String): Result<List<LookItem>> =
        apiDataSource.getSearchMulti(queryKey)

    override suspend fun getFind(imdbID: String): Result<FindResult> = apiDataSource.getFind(imdbID)

    // FirebaseDataSource
    override fun getLiveWatchList(imdbID: String, userID: String): MutableLiveData<Watch> =
        firebaseDataSource.getLiveWatchList(imdbID, userID)

    override fun getLiveWatchListByUser(userID: String): MutableLiveData<List<Watch>> =
        firebaseDataSource.getLiveWatchListByUser(userID)

    override suspend fun pushMultiWatchListExpiration(
        imdbID: String,
        userID: String,
        expiration: Timestamp
    ): Result<Boolean> = firebaseDataSource.pushMultiWatchListExpiration(imdbID, userID, expiration)

    override suspend fun pushSingleWatchListExpiration(watch: Watch): Result<Boolean> =
        firebaseDataSource.pushSingleWatchListExpiration(watch)

    override suspend fun getScores(imdbID: String): Result<List<Score>> =
        firebaseDataSource.getScores(imdbID)

    override suspend fun getScore(imdbID: String, userID: String): Result<Score> =
        firebaseDataSource.getScore(imdbID, userID)

    override fun getLiveScore(imdbID: String, userID: String): MutableLiveData<Score> =
        firebaseDataSource.getLiveScore(imdbID, userID)

    override suspend fun pushUserInfo(user: User): Result<Boolean> =
        firebaseDataSource.pushUserInfo(user)

    override suspend fun getUserByToken(token: String): Result<User> =
        firebaseDataSource.getUserByToken(token)

    override suspend fun getUserById(id: String): Result<User?> = firebaseDataSource.getUserById(id)

    override suspend fun getUsersByIdList(idList: List<String>): Result<List<User>> =
        firebaseDataSource.getUsersByIdList(idList)

    override suspend fun getComments(imdbID: String): Result<List<Comment>> =
        firebaseDataSource.getComments(imdbID)

    override fun getLiveComments(imdbID: String): MutableLiveData<List<Comment>> =
        firebaseDataSource.getLiveComments(imdbID)

    override fun getLivePersonalComments(userID: String): MutableLiveData<List<Comment>> =
        firebaseDataSource.getLivePersonalComments(userID)

    override suspend fun pushComment(comment: Comment): Result<Boolean> =
        firebaseDataSource.pushComment(comment)

    override suspend fun delete(comment: Comment): Result<Boolean> =
        firebaseDataSource.delete(comment)

    override fun getLivePersonalFavorites(userID: String): MutableLiveData<List<String>> =
        firebaseDataSource.getLivePersonalFavorites(userID)

    override fun getLiveCommentsExcludeBlocks(
        imdbID: String,
        blocks: List<String>
    ): MutableLiveData<List<Comment>> =
        firebaseDataSource.getLiveCommentsExcludeBlocks(imdbID, blocks)

    override suspend fun pushWatchedMovie(imdbID: String, userID: String): Result<Boolean> =
        firebaseDataSource.pushWatchedMovie(imdbID, userID)

    override suspend fun removeWatchedMovie(imdbID: String, userID: String): Result<Boolean> =
        firebaseDataSource.removeWatchedMovie(imdbID, userID)

    override suspend fun pushLikedMovie(imdbID: String, userID: String): Result<Boolean> =
        firebaseDataSource.pushLikedMovie(imdbID, userID)

    override suspend fun removeLikedMovie(imdbID: String, userID: String): Result<Boolean> =
        firebaseDataSource.removeLikedMovie(imdbID, userID)

    override suspend fun pushWatchlistMovie(watch: Watch): Result<Boolean> =
        firebaseDataSource.pushWatchlistMovie(watch)

    override suspend fun removeWatchlistMovie(imdbID: String, userID: String): Result<Boolean> =
        firebaseDataSource.removeWatchlistMovie(imdbID, userID)

    override suspend fun pushScore(score: Score): Result<Boolean> =
        firebaseDataSource.pushScore(score)

    override suspend fun pushReport(report: Report): Result<Boolean> =
        firebaseDataSource.pushReport(report)

    override suspend fun pushBlockUser(userID: String, blockedID: String): Result<Boolean> =
        firebaseDataSource.pushBlockUser(userID, blockedID)

    override suspend fun pushPopularMovies(trends: List<Trend>): Result<Boolean> =
        firebaseDataSource.pushPopularMovies(trends)

    override suspend fun pushMockComment(): Result<Boolean> = localDataSource.pushMockComment()

    override suspend fun pushMockScore(): Result<Boolean> = localDataSource.pushMockScore()

    override suspend fun pushMockUser(): Result<Boolean> = localDataSource.pushMockUser()
}
