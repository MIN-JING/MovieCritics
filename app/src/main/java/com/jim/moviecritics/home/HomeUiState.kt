package com.jim.moviecritics.home

import com.jim.moviecritics.data.HomeItem

data class HomeUiState(
    val loadingPopular: Boolean = false,
    val openingDetail: Boolean = false,
    val homeItems: List<HomeItem> = emptyList(),
    val errorMessage: String? = null,
) {
    val loading: Boolean
        get() = loadingPopular || openingDetail
}
