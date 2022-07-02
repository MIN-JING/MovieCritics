package com.jim.moviecritics.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
//import android.net.NetworkInfo
import android.os.Build.*
import com.jim.moviecritics.MovieApplication

object Util {

    fun isInternetConnected(): Boolean {
        val connectivityManager = MovieApplication.instance
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

//        val networkCallback = ConnectivityManager.NetworkCallback()

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

//        val activeNetwork = connectivityManager.activeNetwork
//        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
//        val activeNetwork = cm.activeNetwork
//        return activeNetwork?.isConnectedOrConnecting == true
    }

    fun getString(resourceId: Int): String {
        return MovieApplication.instance.getString(resourceId)
    }
}