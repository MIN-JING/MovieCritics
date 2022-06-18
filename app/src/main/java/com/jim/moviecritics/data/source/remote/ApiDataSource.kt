package com.jim.moviecritics.data.source.remote


import androidx.lifecycle.MutableLiveData
import com.jim.moviecritics.R
import com.jim.moviecritics.data.Comment
import com.jim.moviecritics.data.HomeItem
import com.jim.moviecritics.data.PopularMoviesResult
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.source.ApplicationDataSource
import com.jim.moviecritics.network.TmdbApi
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util


object ApiDataSource : ApplicationDataSource {

    override suspend fun getPopularMovies(): Result<List<HomeItem>> {
//        override suspend fun getPopularMovies(): Result<PopularMoviesResult> {

        if (!Util.isInternetConnected()) {
            return Result.Fail(Util.getString(R.string.internet_not_connected))
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

    override suspend fun loadMockDataComment(): Result<Comment> {
        TODO("Not yet implemented")
    }
}