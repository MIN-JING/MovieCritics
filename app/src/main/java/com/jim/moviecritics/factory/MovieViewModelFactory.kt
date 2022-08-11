package com.jim.moviecritics.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jim.moviecritics.data.Movie
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.detail.DetailViewModel
import com.jim.moviecritics.pending.PendingViewModel
import com.jim.moviecritics.review.ReviewViewModel
import com.jim.moviecritics.trailer.TrailerViewModel

@Suppress("UNCHECKED_CAST")
class MovieViewModelFactory(
    private val applicationRepository: Repository,
    private val movie: Movie
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(DetailViewModel::class.java) ->
                    DetailViewModel(applicationRepository, movie)

                isAssignableFrom(PendingViewModel::class.java) ->
                    PendingViewModel(applicationRepository, movie)

                isAssignableFrom(ReviewViewModel::class.java) ->
                    ReviewViewModel(applicationRepository, movie)

                isAssignableFrom(TrailerViewModel::class.java) ->
                    TrailerViewModel(applicationRepository, movie)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
