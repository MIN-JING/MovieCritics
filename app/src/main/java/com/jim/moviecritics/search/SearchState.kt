package com.jim.moviecritics.search

import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import com.jim.moviecritics.data.LookItem

class SearchState(
    query: TextFieldValue,
    focused: Boolean,
    searching: Boolean,
    suggestions: List<LookItem>,
    searchResults: List<LookItem>
) {
    var query by mutableStateOf(query)
    var focused by mutableStateOf(focused)
    var searching by mutableStateOf(searching)
    var suggestions by mutableStateOf(suggestions)
    var searchResults by mutableStateOf(searchResults)

    val searchDisplay: SearchDisplay
        get() = when {
            !focused && query.text.isEmpty() -> SearchDisplay.INITIAL_RESULTS
            focused && query.text.isEmpty() -> SearchDisplay.SUGGESTIONS
            searchResults.isEmpty() -> SearchDisplay.NO_RESULTS
            else -> SearchDisplay.RESULTS
        }

    override fun toString(): String {
        return "🚀 State query: $query, focused: $focused, searching: $searching " +
                "suggestions: ${suggestions.size}, " +
                "searchResults: ${searchResults.size}, " +
                " searchDisplay: $searchDisplay"
    }
}

@Composable
fun rememberSearchState(
    query: TextFieldValue = TextFieldValue(""),
    focused: Boolean = false,
    searching: Boolean = false,
    suggestions: List<LookItem> = emptyList(),
    searchResults: List<LookItem> = emptyList()
): SearchState {
    return remember {
        SearchState(
            query = query,
            focused = focused,
            searching = searching,
            suggestions = suggestions,
            searchResults = searchResults
        )
    }
}