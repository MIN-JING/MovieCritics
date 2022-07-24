package com.jim.moviecritics

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
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

        const val CHANNEL_ID = "watchlist_reminder_id"
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // WorkManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun isLiveDataDesign() = true
}
