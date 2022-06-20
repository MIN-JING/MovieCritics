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
            val listResult = TmdbApi.retrofitService.getPopularMovies()

            listResult.error?.let {
                return Result.Fail(it)
            }
            Result.Success(listResult.toHomeItems())
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
            val movieResult = TmdbApi.retrofitService.getMovieDetail(id)

            movieResult.error?.let {
                return Result.Fail(it)
            }
            Result.Success(movieResult)

        } catch (e: Exception) {
            Logger.w("[${this::class.simpleName}] exception=${e.message}")
            Result.Error(e)
        }
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

    override suspend fun pushPopularMovies(pushTrend: PushTrend): Result<Boolean> {
        TODO("Not yet implemented")
    }
}