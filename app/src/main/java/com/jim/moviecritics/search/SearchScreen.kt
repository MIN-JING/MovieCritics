package com.jim.moviecritics.search

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jim.moviecritics.R
import com.jim.moviecritics.data.LookItem
import com.jim.moviecritics.util.GlideImage
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    navigateToDetail: (String) -> Unit,
    state: SearchState = rememberSearchState()
) {
    val lookItems by viewModel.lookItems.collectAsStateWithLifecycle()

    Surface {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
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
                delay(500)
                viewModel.onSearchQueryChanged(state.query.text)
                state.searchResults = lookItems
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
                        items(lookItems) { result ->
                            Row(
                                modifier = modifier
                                    .fillMaxWidth()
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
                                } ?: ""

                                val overview = when (result) {
                                    is LookItem.LookMovie -> result.look.overview
                                    is LookItem.LookTelevision -> result.look.overview
                                    is LookItem.LookPerson -> result.look.overview
                                } ?: ""

                                val imageUrl = when (result) {
                                    is LookItem.LookMovie -> result.look.posterPath
                                    is LookItem.LookTelevision -> result.look.posterPath
                                    is LookItem.LookPerson -> result.look.profilePath
                                } ?: ""

                                GlideImage(
                                    imageUrl = imageUrl,
                                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop,
                                    placeHolder = R.drawable.ic_movie,
                                    error = R.drawable.ic_error
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.subtitle1,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = overview,
                                        style = MaterialTheme.typography.body2,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))
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