package com.jim.moviecritics.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.profile.ItemFavoriteViewModel
import com.jim.moviecritics.profile.ItemGuideViewModel


@Suppress("UNCHECKED_CAST")
class ProfileItemViewModelFactory(
    private val applicationRepository: ApplicationRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(ItemGuideViewModel::class.java) ->
                    ItemGuideViewModel(applicationRepository)

                isAssignableFrom(ItemFavoriteViewModel::class.java) ->
                    ItemFavoriteViewModel(applicationRepository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}