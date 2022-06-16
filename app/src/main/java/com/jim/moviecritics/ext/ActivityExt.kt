package com.jim.moviecritics.ext

import android.app.Activity
import android.view.Gravity
import android.widget.Toast
import com.jim.moviecritics.MovieApplication
import com.jim.moviecritics.factory.ViewModelFactory

fun Activity.getVmFactory(): ViewModelFactory {
    val repository = (applicationContext as MovieApplication).applicationRepository
    return ViewModelFactory(repository)
}

fun Activity?.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}