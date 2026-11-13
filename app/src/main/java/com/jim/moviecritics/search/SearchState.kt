package com.jim.moviecritics.search

import androidx.compose.ui.text.input.TextFieldValue
import com.jim.moviecritics.data.LookItem

enum class SearchFilter {
    ALL,
    MOVIE,
    TV,
    PERSON
}

data class SearchUiState(
    val query: TextFieldValue = TextFieldValue(""),
    val focused: Boolean = false,
    val searching: Boolean = false,
    val hasSearched: Boolean = false,
    val rawResults: List<LookItem> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val suggestedQueries: List<String> = defaultSuggestedQueries(),
    val selectedFilter: SearchFilter = SearchFilter.ALL,
    val errorMessage: String? = null,
) {
    val searchResults: List<LookItem>
        get() = when (selectedFilter) {
            SearchFilter.ALL -> rawResults
            SearchFilter.MOVIE -> rawResults.filterIsInstance<LookItem.LookMovie>()
            SearchFilter.TV -> rawResults.filterIsInstance<LookItem.LookTelevision>()
            SearchFilter.PERSON -> rawResults.filterIsInstance<LookItem.LookPerson>()
        }

    val searchDisplay: SearchDisplay
        get() = when {
            !focused && query.text.isBlank() -> SearchDisplay.INITIAL_RESULTS
            focused && query.text.isBlank() -> SearchDisplay.SUGGESTIONS
            hasSearched && !searching && errorMessage == null && searchResults.isEmpty() -> {
                SearchDisplay.NO_RESULTS
            }
            else -> SearchDisplay.RESULTS
        }
}

private fun defaultSuggestedQueries(): List<String> {
    return listOf(
        "Dune",
        "Interstellar",
        "The Dark Knight",
        "Breaking Bad",
        "Emma Stone",
    )
}
