package com.jim.moviecritics.ext

import androidx.fragment.app.Fragment
import com.jim.moviecritics.MovieApplication
import com.jim.moviecritics.data.Movie
import com.jim.moviecritics.data.MovieDetailResult
import com.jim.moviecritics.factory.MovieViewModelFactory
import com.jim.moviecritics.factory.ViewModelFactory


fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as MovieApplication).applicationRepository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(movie: Movie): MovieViewModelFactory {
    val repository = (requireContext().applicationContext as MovieApplication).applicationRepository
    return MovieViewModelFactory(repository, movie)
}