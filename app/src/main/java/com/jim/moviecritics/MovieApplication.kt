package com.jim.moviecritics

import android.app.Application
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.util.ServiceLocator
import kotlin.properties.Delegates


/**
 * An application that lazily provides a repository. Note that this Service Locator pattern is
 * used to simplify the sample. Consider a Dependency Injection framework.
 */
class MovieApplication : Application() {

    // Depends on the flavor
    val applicationRepository: ApplicationRepository
        get() = ServiceLocator.provideRepository(this)

    companion object {
        var instance: MovieApplication by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun isLiveDataDesign() = true
}