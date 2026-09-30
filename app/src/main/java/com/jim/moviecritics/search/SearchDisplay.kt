package com.jim.moviecritics.search

enum class SearchDisplay {
    INITIAL_RESULTS,
    SUGGESTIONS,
    RESULTS,
    NO_RESULTS,
    ERROR
}

fun searchDisplayOf(query: String, uiState: SearchUiState): SearchDisplay = when {
    query.isBlank() -> SearchDisplay.INITIAL_RESULTS
    // Keep showing the previous results while the next search is in flight
    uiState.results.isNotEmpty() -> SearchDisplay.RESULTS
    uiState.searching -> SearchDisplay.INITIAL_RESULTS
    uiState.error != null -> SearchDisplay.ERROR
    else -> SearchDisplay.NO_RESULTS
}
