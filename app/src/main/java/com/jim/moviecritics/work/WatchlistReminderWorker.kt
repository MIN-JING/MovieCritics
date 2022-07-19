package com.jim.moviecritics.work

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.jim.moviecritics.MainActivity
import com.jim.moviecritics.MovieApplication
import com.jim.moviecritics.R
import com.jim.moviecritics.util.Logger


class WatchlistReminderWorker(
    context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {

    // Arbitrary id number
    private val notificationId = 17

    override fun doWork(): Result {
        Logger.i("doWork()")
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }


        val pendingIntent: PendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent
                    .getActivity(
                        applicationContext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            } else {
                PendingIntent
                    .getActivity(applicationContext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT)
            }

        val movieTitle = inputData.getString(nameKey)

        val builder = NotificationCompat.Builder(applicationContext, MovieApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Watch Movie!")
            .setContentText("$movieTitle")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(notificationId, builder.build())
        }

        return Result.success()
    }

    companion object {
        const val nameKey = "MovieTitle"
    }
}