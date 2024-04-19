package com.jim.moviecritics.search

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jim.moviecritics.data.LookItem
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    navigateToDetail: (String) -> Unit,
    state: SearchState = rememberSearchState()
) {
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

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
            )
            LaunchedEffect(state.query.text) {
                state.searching = true
                viewModel.onSearchQueryChanged(state.query.text)
                delay(100)
                state.searchResults = searchResults
                state.searching = false
            }
            when (state.searchDisplay) {
                SearchDisplay.INITIAL_RESULTS -> {
                    Log.i("SearchScreen", "INITIAL_RESULTS")
                }
                SearchDisplay.NO_RESULTS -> {
                    Log.i("SearchScreen", "NO_RESULTS")
                }

                SearchDisplay.SUGGESTIONS -> {
                    Log.i("SearchScreen", "SUGGESTIONS")
                }

                SearchDisplay.RESULTS -> {
                    Log.i("SearchScreen", "RESULTS")

                    LazyColumn {
//                        items(searchResults) { result ->
//                            SearchResultItem(
//                                lookItem = result,
//                                onClick = { navigateToDetail(result.id) }
//                            )
//                        }
                        items(searchResults) { result ->
                            Row(
                                modifier = modifier
                                    .fillMaxWidth()
//                                    .clickable(onClick = onClick)
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val title = when (result) {
                                    is LookItem.LookMovie -> {
                                        result.look.title
                                    }
                                    is LookItem.LookTelevision -> {
                                        result.look.title
                                    }
                                    is LookItem.LookPerson -> {
                                        result.look.title
                                    }
                                    else -> {
                                        "Unknown"
                                    }
                                } ?: ""
                                // Display the item details
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.subtitle1
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                // Add more item details as needed
                            }
                        }
                    }
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