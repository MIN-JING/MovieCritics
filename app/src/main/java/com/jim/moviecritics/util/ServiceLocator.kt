package com.jim.moviecritics.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.jim.moviecritics.data.source.DataSource
import com.jim.moviecritics.data.source.DefaultRepository
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.data.source.local.LocalDataSource
import com.jim.moviecritics.data.source.remote.ApiDataSource
import com.jim.moviecritics.data.source.remote.FirebaseDataSource

object ServiceLocator {

    @Volatile
    var repository: Repository? = null
        @VisibleForTesting set

    fun provideRepository(context: Context): Repository {
        synchronized(this) {
            return repository
                ?: createApplicationRepository(context)
        }
    }

    private fun createApplicationRepository(context: Context): Repository {
        return DefaultRepository(
            ApiDataSource,
            FirebaseDataSource,
            createLocalDataSource(context)
        )
    }

    private fun createLocalDataSource(context: Context): DataSource {
        return LocalDataSource(context)
    }
}
