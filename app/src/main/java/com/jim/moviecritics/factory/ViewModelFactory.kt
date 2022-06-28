package com.jim.moviecritics.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jim.moviecritics.MainViewModel
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.watchlist.WatchlistViewModel
import com.jim.moviecritics.home.HomeViewModel
import com.jim.moviecritics.profile.ProfileViewModel
import com.jim.moviecritics.search.SearchViewModel


@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val applicationRepository: ApplicationRepository,
//    private val movieDetailResult: MovieDetailResult
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(applicationRepository)

                isAssignableFrom(HomeViewModel::class.java) ->
                    HomeViewModel(applicationRepository)

                isAssignableFrom(SearchViewModel::class.java) ->
                    SearchViewModel(applicationRepository)

                isAssignableFrom(WatchlistViewModel::class.java) ->
                    WatchlistViewModel(applicationRepository)

                isAssignableFrom(ProfileViewModel::class.java) ->
                    ProfileViewModel(applicationRepository)

//                isAssignableFrom(DetailViewModel::class.java) ->
//                    DetailViewModel(applicationRepository, movieDetailResult)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}