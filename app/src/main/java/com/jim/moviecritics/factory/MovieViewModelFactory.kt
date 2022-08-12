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
    private val repository: Repository,
    private val movie: Movie
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(DetailViewModel::class.java) ->
                    DetailViewModel(repository, movie)

                isAssignableFrom(PendingViewModel::class.java) ->
                    PendingViewModel(repository, movie)

                isAssignableFrom(ReviewViewModel::class.java) ->
                    ReviewViewModel(repository, movie)

                isAssignableFrom(TrailerViewModel::class.java) ->
                    TrailerViewModel(repository, movie)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}