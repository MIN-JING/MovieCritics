package com.jim.moviecritics.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.jim.moviecritics.MovieApplication

object Util {

    fun isInternetConnected(): Boolean {
        val cm = MovieApplication.instance
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
//        val activeNetwork = cm.activeNetwork
        return activeNetwork?.isConnectedOrConnecting == true
    }

    fun getString(resourceId: Int): String {
        return MovieApplication.instance.getString(resourceId)
    }
}