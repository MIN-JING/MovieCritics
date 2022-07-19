package com.jim.moviecritics.ext

import android.app.Activity
import android.widget.Toast
import com.jim.moviecritics.MovieApplication
import com.jim.moviecritics.factory.ViewModelFactory

fun Activity.getVmFactory(): ViewModelFactory {
    val repository = (applicationContext as MovieApplication).applicationRepository
    return ViewModelFactory(repository)
}

fun Activity?.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).apply {
// starting from Android R, text Toast can no longer be set with these parameters
// since the setter functions have become no-op functions
// e.g. setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}