package com.jim.moviecritics.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jim.moviecritics.MainViewModel
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.home.HomeViewModel
import com.jim.moviecritics.login.LoginViewModel
import com.jim.moviecritics.profile.item.ItemFavoriteViewModel
import com.jim.moviecritics.profile.item.ItemGuideViewModel
import com.jim.moviecritics.search.SearchViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val repository: Repository,
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(repository)

                isAssignableFrom(HomeViewModel::class.java) ->
                    HomeViewModel(repository)

                isAssignableFrom(SearchViewModel::class.java) ->
                    SearchViewModel(repository)

                isAssignableFrom(LoginViewModel::class.java) ->
                    LoginViewModel(repository)

                isAssignableFrom(ItemGuideViewModel::class.java) ->
                    ItemGuideViewModel(repository)

                isAssignableFrom(ItemFavoriteViewModel::class.java) ->
                    ItemFavoriteViewModel(repository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}