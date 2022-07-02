package com.jim.moviecritics.profile

import androidx.lifecycle.ViewModel
import com.jim.moviecritics.data.source.ApplicationRepository

class ItemFavoriteViewModel(
    private val applicationRepository: ApplicationRepository,
    private val profileType: ProfileTypeFilter
) : ViewModel() {
}