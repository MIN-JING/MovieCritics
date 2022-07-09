package com.jim.moviecritics.data.source.remote


import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.jim.moviecritics.R
import com.jim.moviecritics.data.*
import com.jim.moviecritics.data.source.ApplicationDataSource
import com.jim.moviecritics.network.TmdbApi
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util.getString
import com.jim.moviecritics.util.Util.isInternetConnected


object ApiDataSource : ApplicationDataSource {

    override suspend fun getPopularMovies(): Result<List<HomeItem>> {

        if (!isInternetConnected()) {
            return Result.Fail(getString(R.string.internet_not_connected))
        }

        return try {
            // this will run on a thread managed by Retrofit
            val popularResult = TmdbApi.retrofitService.getPopularMovies()

            popularResult.error?.let {
                return Result.Fail(it)
            }
            Result.Success(popularResult.toHomeItems())

        } catch (e: Exception) {
            Logger.w("[${this::class.simpleName}] exception=${e.message}")
            Result.Error(e)
        }
    }

    override suspend fun getMovieDetail(id: Int): Result<MovieDetailResult> {

        if (!isInternetConnected()) {
            return Result.Fail(getString(R.string.internet_not_connected))
        }

        return try {
            val detailResult = TmdbApi.retrofitService.getMovieDetail(id)

            detailResult.error?.let {
                return Result.Fail(it)
            }
            Result.Success(detailResult)

        } catch (e: Exception) {
            Logger.w("[${this::class.simpleName}] exception=${e.message}")
            Result.Error(e)
        }
    }

    override suspend fun getMovieCredit(id: Int): Result<CreditResult> {
        if (!isInternetConnected()) {
            return Result.Fail(getString(R.string.internet_not_connected))
        }

        return try {
            val creditResult = TmdbApi.retrofitService.getMovieCredit(id)

            creditResult.error?.let {
                return Result.Fail(it)
            }
            Result.Success(creditResult)

        } catch (e: Exception) {
            Logger.w("[${this::class.simpleName}] exception=${e.message}")
            Result.Error(e)
        }
    }

    override suspend fun getSearchMulti(queryKey: String): Result<List<LookItem>> {
        if (!isInternetConnected()) {
            return Result.Fail(getString(R.string.internet_not_connected))
        }

        return try {
            val searchResult = TmdbApi.retrofitService.getSearchMulti(queryKey)

            searchResult.error?.let {
                return  Result.Fail(it)
            }
            Result.Success(searchResult.toLookItems())
        } catch (e: Exception) {
            Logger.w("[${this::class.simpleName}] exception=${e.message}")
            Result.Error(e)
        }
    }

    override suspend fun getFind(imdbID: String): Result<FindResult> {
        if (!isInternetConnected()) {
            return Result.Fail(getString(R.string.internet_not_connected))
        }

        return try {
            val searchResult = TmdbApi.retrofitService.getFind(imdbID)

            searchResult.error?.let {
                return Result.Fail(it)
            }
            Result.Success(searchResult)

        } catch (e: Exception) {
            Logger.w("[${this::class.simpleName}] exception=${e.message}")
            Result.Error(e)
        }
    }

    override fun getLiveWatchList(imdbID: String, userID: String): MutableLiveData<Watch> {
        TODO("Not yet implemented")
    }

    override fun getLiveWatchListByUser(userID: String): MutableLiveData<List<Watch>> {
        TODO("Not yet implemented")
    }

    override suspend fun pushWatchListExpiration(
        imdbID: String,
        userID: String,
        expiration: Timestamp,
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getScores(imdbID: String): Result<List<Score>> {
        TODO("Not yet implemented")
    }

    override suspend fun getScore(imdbID: String, userID: String): Result<Score> {
        TODO("Not yet implemented")
    }

    override fun getLiveScore(imdbID: String, userID: String): MutableLiveData<Score> {
        TODO("Not yet implemented")
    }

    override suspend fun pushUserInfo(user: User): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserByToken(token: String): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserById(id: String): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun getComments(imdbID: String): Result<List<Comment>> {
        TODO("Not yet implemented")
    }

    override fun getLiveComments(imdbID: String): MutableLiveData<List<Comment>> {
        TODO("Not yet implemented")
    }

    override fun getLivePersonalComments(userID: String): MutableLiveData<List<Comment>> {
        TODO("Not yet implemented")
    }

    override suspend fun pushComment(comment: Comment): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(comment: Comment): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getLivePersonalFavorites(userID: String): MutableLiveData<List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun pushWatchedMovie(imdbID: String, userID: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun removeWatchedMovie(imdbID: String, userID: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushLikedMovie(imdbID: String, userID: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun removeLikedMovie(imdbID: String, userID: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushWatchlistMovie(watch: Watch): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun removeWatchlistMovie(imdbID: String, userID: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushScore(score: Score): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushPopularMovies(trends: List<Trend>): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushMockComment(): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushMockScore(): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushMockUser(): Result<Boolean> {
        TODO("Not yet implemented")
    }
}