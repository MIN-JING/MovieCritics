package com.jim.moviecritics.data.source

import androidx.lifecycle.MutableLiveData
import com.jim.moviecritics.data.*
import com.jim.moviecritics.data.source.remote.FirebaseDataSource

/**
 * Concrete implementation to load Application sources.
 */
class DefaultApplicationRepository(
    private val apiDataSource: ApplicationDataSource,
    private val firebaseDataSource: ApplicationDataSource,
    private val localDataSource: ApplicationDataSource ) : ApplicationRepository {

    override suspend fun getPopularMovies(): Result<List<HomeItem>> {
        return apiDataSource.getPopularMovies()
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

    override suspend fun loadMockDataComment(): Result<Comment> {
        return localDataSource.loadMockDataComment()
    }

    override suspend fun pushPopularMovies(pushTrend: PushTrend): Result<Boolean> {
        return FirebaseDataSource.pushPopularMovies(pushTrend)
    }

}