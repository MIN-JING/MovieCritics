package com.jim.moviecritics.search

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jim.moviecritics.R
import com.jim.moviecritics.data.Look
import com.jim.moviecritics.data.LookItem
import com.jim.moviecritics.data.Movie
import com.jim.moviecritics.util.GlideImage

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateToDetail: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    uiState.navigateToDetail?.let { movie ->
        LaunchedEffect(movie) {
            onNavigateToDetail(movie)
            viewModel.onDetailNavigated()
        }
    }

    uiState.userMessage?.let { message ->
        LaunchedEffect(message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onUserMessageShown()
        }
    }

    SearchContent(
        query = viewModel.query,
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onItemClick = viewModel::onItemClick,
        modifier = modifier
    )
}

@Composable
fun SearchContent(
    query: String,
    uiState: SearchUiState,
    onQueryChange: (String) -> Unit,
    onItemClick: (LookItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column {
            SearchTextField(
                query = query,
                onQueryChange = onQueryChange,
                onClearQuery = { onQueryChange("") },
                searching = uiState.searching
            )

            if (uiState.loadingDetail) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            when (searchDisplayOf(query, uiState)) {
                SearchDisplay.INITIAL_RESULTS, SearchDisplay.SUGGESTIONS -> Unit

                SearchDisplay.RESULTS -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.results) { item ->
                            SearchResultItem(
                                item = item,
                                enabled = !uiState.loadingDetail,
                                onClick = { onItemClick(item) }
                            )
                        }
                    }
                }

                SearchDisplay.NO_RESULTS -> {
                    SearchMessage(text = stringResource(R.string.search_no_results, query.trim()))
                }

                SearchDisplay.ERROR -> {
                    SearchMessage(text = uiState.error.orEmpty())
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    item: LookItem,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val look = item.look
    // TMDB returns `title` for movies and `name` for TV shows and people
    val title = look.title ?: look.name ?: ""
    val description = when (item) {
        is LookItem.LookPerson ->
            look.knownFor
                ?.mapNotNull { it.title ?: it.name }
                ?.takeIf { it.isNotEmpty() }
                ?.let { stringResource(R.string.search_known_for, it.joinToString()) }
        else -> look.overview
    } ?: ""
    val imageUrl = when (item) {
        is LookItem.LookPerson -> look.profilePath
        else -> look.posterPath
    } ?: ""

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GlideImage(
            imageUrl = imageUrl,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeHolder = R.drawable.ic_movie,
            error = R.drawable.ic_error
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SearchMessage(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}

private val previewResults = listOf(
    LookItem.LookMovie(
        Look(mediaType = "movie", title = "Movie 1", overview = "Overview 1")
    ),
    LookItem.LookTelevision(
        Look(mediaType = "tv", name = "TV Show 1", overview = "Overview 2")
    ),
    LookItem.LookPerson(
        Look(mediaType = "person", name = "Person 1")
    )
)

@Preview(showBackground = true, name = "Search Results")
@Composable
private fun SearchResultsPreview() {
    MaterialTheme {
        SearchContent(
            query = "movie",
            uiState = SearchUiState(results = previewResults),
            onQueryChange = {},
            onItemClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Search No Results")
@Composable
private fun SearchNoResultsPreview() {
    MaterialTheme {
        SearchContent(
            query = "zzzz",
            uiState = SearchUiState(),
            onQueryChange = {},
            onItemClick = {}
        )
    }
}
