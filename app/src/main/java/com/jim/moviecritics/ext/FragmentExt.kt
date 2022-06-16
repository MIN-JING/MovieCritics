package com.jim.moviecritics.ext

import androidx.fragment.app.Fragment
import com.jim.moviecritics.MovieApplication
import com.jim.moviecritics.factory.ViewModelFactory


fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as MovieApplication).applicationRepository
    return ViewModelFactory(repository)
}