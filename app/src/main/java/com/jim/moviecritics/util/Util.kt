package com.jim.moviecritics.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build.*
import com.jim.moviecritics.MovieApplication

object Util {

    fun isInternetConnected(): Boolean {
        val connectivityManager = MovieApplication.instance
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (VERSION.SDK_INT >= VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork
            if (activeNetwork != null) {
                val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                if (networkCapabilities != null) {
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                }
            }
            false
        } else {
            @Suppress("DEPRECATION") val netInfo = connectivityManager.activeNetworkInfo

            @Suppress("DEPRECATION")
            netInfo != null && netInfo.isConnectedOrConnecting
        }
    }

    fun getString(resourceId: Int): String {
        return MovieApplication.instance.getString(resourceId)
    }
}