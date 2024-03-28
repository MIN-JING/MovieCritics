package com.jim.moviecritics.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    navigateToDetail: (String) -> Unit,
    state: SearchState = rememberSearchState()
) {
    Surface {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            Text("Hello, SearchScreen!")
            SearchTextField(
                query = state.query,
                onQueryChange = { state.query = it },
                onSearchFocusChange = { state.focused = it },
                onClearQuery = { state.query = TextFieldValue("") },
                searching = state.searching,
                focused = state.focused,
                modifier = modifier.weight(1f)
            )
            LaunchedEffect(state.query.text) {
                state.searching = true
                delay(100)
                state.searchResults = emptyList()
                state.searching = false
            }
            when (state.searchDisplay) {
                SearchDisplay.INITIAL_RESULTS -> {

                }
                SearchDisplay.NO_RESULTS -> {

                }

                SearchDisplay.SUGGESTIONS -> {

                }

                SearchDisplay.RESULTS -> {

                }
            }
        }
    }
}

//@Preview(showBackground = true, name = "Text preview")
//@Composable
//fun SearchScreenPreview() {
//    MaterialTheme {
//        SearchScreen()
//    }
//}