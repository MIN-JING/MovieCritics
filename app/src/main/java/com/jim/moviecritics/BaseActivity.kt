package com.jim.moviecritics

import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.DisplayCutout
import androidx.appcompat.app.AppCompatActivity
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

open class BaseActivity : AppCompatActivity(), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    suspend fun getCutoutHeight(): Int {
        return withContext(Dispatchers.IO) {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {

                    window?.let {
                        val displayCutout: DisplayCutout? = it.decorView.rootWindowInsets.displayCutout
                        Log.d("Jim", "displayCutout?.safeInsetTop=${displayCutout?.safeInsetTop}")
                        Log.d("Jim", "displayCutout?.safeInsetBottom=${displayCutout?.safeInsetBottom}")
                        Log.d("Jim", "displayCutout?.safeInsetLeft=${displayCutout?.safeInsetLeft}")
                        Log.d("Jim", "displayCutout?.safeInsetRight=${displayCutout?.safeInsetRight}")

                        val rects: List<Rect>? = displayCutout?.boundingRects
                        Log.d("Jim", "rects?.size=${rects?.size}")
                        Log.d("Jim", "rects=$rects")

                        displayCutout?.safeInsetTop ?: 0
                    } ?: 0
                }
                else -> 0
            }
        }
    }
}
