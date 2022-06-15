package com.jim.moviecritics.data.source

import androidx.lifecycle.MutableLiveData
import com.jim.moviecritics.data.Comment
import com.jim.moviecritics.data.Result

/**
 * Concrete implementation to load Application sources.
 */
class DefaultApplicationRepository(
    private val remoteDataSource: ApplicationDataSource,
    private val localDataSource: ApplicationDataSource) : ApplicationRepository {

    override suspend fun getComments(): Result<List<Comment>> {
        return remoteDataSource.getComments()
    }

    override fun getLiveComments(): MutableLiveData<List<Comment>> {
        return remoteDataSource.getLiveComments()
    }

    override suspend fun comment(comment: Comment): Result<Boolean> {
        return remoteDataSource.comment(comment)
    }

    override suspend fun delete(comment: Comment): Result<Boolean> {
        return remoteDataSource.delete(comment)
    }

}