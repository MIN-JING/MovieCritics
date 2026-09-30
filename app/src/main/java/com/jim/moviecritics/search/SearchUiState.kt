package com.jim.moviecritics.search

import androidx.annotation.StringRes
import com.jim.moviecritics.data.LookItem
import com.jim.moviecritics.data.Movie

data class SearchUiState(
    val results: List<LookItem> = emptyList(),
    val searching: Boolean = false,
    val error: String? = null,
    val loadingDetail: Boolean = false,
    val navigateToDetail: Movie? = null,
    @StringRes val userMessage: Int? = null
)
