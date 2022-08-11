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
    private val applicationRepository: Repository,
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

                isAssignableFrom(LoginViewModel::class.java) ->
                    LoginViewModel(applicationRepository)

                isAssignableFrom(ItemGuideViewModel::class.java) ->
                    ItemGuideViewModel(applicationRepository)

                isAssignableFrom(ItemFavoriteViewModel::class.java) ->
                    ItemFavoriteViewModel(applicationRepository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
