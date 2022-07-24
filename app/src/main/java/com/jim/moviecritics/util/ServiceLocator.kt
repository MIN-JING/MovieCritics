package com.jim.moviecritics.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.jim.moviecritics.data.source.ApplicationDataSource
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.data.source.DefaultApplicationRepository
import com.jim.moviecritics.data.source.local.LocalDataSource
import com.jim.moviecritics.data.source.remote.ApiDataSource
import com.jim.moviecritics.data.source.remote.FirebaseDataSource

object ServiceLocator {

    @Volatile
    var repository: ApplicationRepository? = null
        @VisibleForTesting set

    fun provideRepository(context: Context): ApplicationRepository {
        synchronized(this) {
            return repository
                ?: repository
                ?: createApplicationRepository(context)
        }
    }

    private fun createApplicationRepository(context: Context): ApplicationRepository {
        return DefaultApplicationRepository(
            ApiDataSource,
            FirebaseDataSource,
            createLocalDataSource(context)
        )
    }

    private fun createLocalDataSource(context: Context): ApplicationDataSource {
        return LocalDataSource(context)
    }
}
