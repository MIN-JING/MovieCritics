package com.jim.moviecritics.data.source.remote


import androidx.lifecycle.MutableLiveData
import com.jim.moviecritics.R
import com.jim.moviecritics.data.*
import com.jim.moviecritics.data.source.ApplicationDataSource
import com.jim.moviecritics.network.TmdbApi
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util.getString
import com.jim.moviecritics.util.Util.isInternetConnected


object ApiDataSource : ApplicationDataSource {

    override suspend fun getPopularMovies(): Result<List<HomeItem>> {
//        override suspend fun getPopularMovies(): Result<PopularMoviesResult> {

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
//            Result.Success(listResult)

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

    override suspend fun getScores(imdbID: String): Result<List<Score>> {
        TODO("Not yet implemented")
    }

    override suspend fun getScore(imdbID: String, userID: Long): Result<Score> {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(userID: Long): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun getComments(): Result<List<Comment>> {
        TODO("Not yet implemented")
    }

    override fun getLiveComments(): MutableLiveData<List<Comment>> {
        TODO("Not yet implemented")
    }

    override suspend fun comment(comment: Comment): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(comment: Comment): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun loadMockComment(): Comment {
        TODO("Not yet implemented")
    }

    override fun loadMockScore(): Score {
        TODO("Not yet implemented")
    }

    override suspend fun pushWatchedMovie(imdbID: String, userID: Long): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushPopularMovies(pushTrend: PushTrend): Result<Boolean> {
        TODO("Not yet implemented")
    }
}