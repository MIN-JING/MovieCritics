package com.jim.moviecritics.util

import android.util.Log
import com.jim.moviecritics.BuildConfig

object Logger {
    private const val TAG = "Jim-MovieCritics"

    fun v(content: String) { if (BuildConfig.LOGGER_VISIBLE) Log.v(TAG, content) }
    fun d(content: String) { if (BuildConfig.LOGGER_VISIBLE) Log.d(TAG, content) }
    fun i(content: String) { if (BuildConfig.LOGGER_VISIBLE) Log.i(TAG, content) }
    fun w(content: String) { if (BuildConfig.LOGGER_VISIBLE) Log.w(TAG, content) }
    fun e(content: String) { if (BuildConfig.LOGGER_VISIBLE) Log.e(TAG, content) }
}
