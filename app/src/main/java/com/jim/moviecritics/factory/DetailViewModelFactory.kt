package com.jim.moviecritics.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jim.moviecritics.data.Movie
import com.jim.moviecritics.data.User
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.detail.DetailViewModel


@Suppress("UNCHECKED_CAST")
class DetailViewModelFactory(
    private val applicationRepository: ApplicationRepository,
    private val movie: Movie,
    private val user: User?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
//                isAssignableFrom(DetailViewModel::class.java) ->
//                    DetailViewModel(applicationRepository, movie, user)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}