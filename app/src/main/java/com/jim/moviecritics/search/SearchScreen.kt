package com.jim.moviecritics.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jim.moviecritics.R
import com.jim.moviecritics.data.KnownFor
import com.jim.moviecritics.data.Look
import com.jim.moviecritics.data.LookItem
import com.jim.moviecritics.util.GlideImage
import java.util.Locale

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface {
        Column(
            modifier = modifier.fillMaxSize(),
        ) {
            SearchTextField(
                query = uiState.query,
                onQueryChange = viewModel::onQueryChange,
                onSearchFocusChange = viewModel::onSearchFocusChange,
                onClearQuery = viewModel::onClearQuery,
                searching = uiState.searching,
                focused = uiState.focused,
            )

            if (uiState.query.text.isNotBlank()) {
                SearchFilterRow(
                    selectedFilter = uiState.selectedFilter,
                    onFilterSelected = viewModel::onFilterSelected,
                )
            }

            if (uiState.searching) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            when {
                uiState.errorMessage != null && uiState.query.text.isNotBlank() -> {
                    SearchErrorContent(
                        message = uiState.errorMessage,
                        onRetry = viewModel::retrySearch,
                    )
                }
                uiState.searchDisplay == SearchDisplay.INITIAL_RESULTS -> {
                    SearchDiscoveryContent(
                        recentSearches = uiState.recentSearches,
                        suggestedQueries = uiState.suggestedQueries,
                        onSuggestionSelected = viewModel::onSuggestionSelected,
                    )
                }
                uiState.searchDisplay == SearchDisplay.SUGGESTIONS -> {
                    SearchSuggestionContent(
                        recentSearches = uiState.recentSearches,
                        suggestedQueries = uiState.suggestedQueries,
                        onSuggestionSelected = viewModel::onSuggestionSelected,
                    )
                }
                uiState.searchDisplay == SearchDisplay.NO_RESULTS -> {
                    SearchNoResultContent(
                        query = uiState.query.text,
                        suggestedQueries = uiState.suggestedQueries,
                        onSuggestionSelected = viewModel::onSuggestionSelected,
                    )
                }
                else -> {
                    SearchResultList(
                        results = uiState.searchResults,
                        onItemClick = viewModel::onResultSelected,
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchFilterRow(
    selectedFilter: SearchFilter,
    onFilterSelected: (SearchFilter) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 16.dp,
            vertical = 12.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(SearchFilter.entries) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(text = filter.toLabel())
                },
            )
        }
    }
}

@Composable
private fun SearchDiscoveryContent(
    recentSearches: List<String>,
    suggestedQueries: List<String>,
    onSuggestionSelected: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            SectionBlock(
                title = stringResource(R.string.search_recent_title),
                emptyText = stringResource(R.string.search_recent_empty),
                items = recentSearches,
                onSuggestionSelected = onSuggestionSelected,
            )
        }
        item {
            SectionBlock(
                title = stringResource(R.string.search_discover_title),
                items = suggestedQueries,
                onSuggestionSelected = onSuggestionSelected,
            )
        }
    }
}

@Composable
private fun SearchSuggestionContent(
    recentSearches: List<String>,
    suggestedQueries: List<String>,
    onSuggestionSelected: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            SectionBlock(
                title = stringResource(R.string.search_recent_title),
                emptyText = stringResource(R.string.search_recent_empty),
                items = recentSearches,
                onSuggestionSelected = onSuggestionSelected,
            )
        }
        item {
            SectionBlock(
                title = stringResource(R.string.search_suggestions_title),
                items = suggestedQueries,
                onSuggestionSelected = onSuggestionSelected,
            )
        }
    }
}

@Composable
private fun SearchNoResultContent(
    query: String,
    suggestedQueries: List<String>,
    onSuggestionSelected: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
    ) {
        Text(
            text = stringResource(R.string.search_no_results_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.search_no_results_body, query),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(24.dp))
        SectionBlock(
            title = stringResource(R.string.search_try_these_title),
            items = suggestedQueries,
            onSuggestionSelected = onSuggestionSelected,
            outerPadding = 0.dp,
        )
    }
}

@Composable
private fun SearchErrorContent(
    message: String?,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.search_error_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = message ?: stringResource(R.string.you_know_nothing),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedButton(onClick = onRetry) {
            Text(text = stringResource(R.string.search_retry))
        }
    }
}

@Composable
private fun SearchResultList(
    results: List<LookItem>,
    onItemClick: (LookItem) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 16.dp,
            top = 8.dp,
            end = 16.dp,
            bottom = 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(results, key = { "${it.mediaType}-${it.id}" }) { item ->
            SearchResultCard(
                item = item,
                onItemClick = { onItemClick(item) },
            )
        }
    }
}

@Composable
private fun SearchResultCard(
    item: LookItem,
    onItemClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlideImage(
                imageUrl = item.imageUrl(),
                modifier = Modifier
                    .size(width = 88.dp, height = 132.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                placeHolder = R.drawable.ic_movie,
                error = R.drawable.ic_error,
            )

            Spacer(modifier = Modifier.size(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                AssistChip(
                    onClick = onItemClick,
                    label = {
                        Text(text = item.mediaTypeLabel())
                    },
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = item.titleText(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                item.subtitleText()?.let { subtitle ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.descriptionText(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun SectionBlock(
    title: String,
    items: List<String>,
    onSuggestionSelected: (String) -> Unit,
    emptyText: String? = null,
    outerPadding: androidx.compose.ui.unit.Dp = 16.dp,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = outerPadding),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (items.isEmpty()) {
            emptyText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            return
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items) { query ->
                AssistChip(
                    onClick = { onSuggestionSelected(query) },
                    label = { Text(text = query) },
                )
            }
        }
    }
}

private fun SearchFilter.toLabel(): String {
    return when (this) {
        SearchFilter.ALL -> "All"
        SearchFilter.MOVIE -> "Movie"
        SearchFilter.TV -> "TV"
        SearchFilter.PERSON -> "Person"
    }
}

private fun LookItem.titleText(): String {
    return when (this) {
        is LookItem.LookMovie -> look.title.orSearchFallback()
        is LookItem.LookTelevision -> (look.name ?: look.title).orSearchFallback()
        is LookItem.LookPerson -> (look.name ?: look.title).orSearchFallback()
    }
}

private fun LookItem.subtitleText(): String? {
    return when (this) {
        is LookItem.LookMovie -> buildMovieSubtitle(look)
        is LookItem.LookTelevision -> buildTvSubtitle(look)
        is LookItem.LookPerson -> buildPersonSubtitle(look.knownFor)
    }
}

private fun LookItem.descriptionText(): String {
    return when (this) {
        is LookItem.LookMovie -> look.overview.orSearchDefaultDescription()
        is LookItem.LookTelevision -> look.overview.orSearchDefaultDescription()
        is LookItem.LookPerson -> {
            val knownFor = look.knownFor
                ?.mapNotNull { it.title ?: it.name }
                ?.take(3)
                ?.joinToString(", ")

            knownFor?.let {
                "Known for $it"
            } ?: stringResourceSafe(R.string.search_person_default_description)
        }
    }
}

private fun LookItem.imageUrl(): String {
    return when (this) {
        is LookItem.LookMovie -> look.posterPath.orEmpty()
        is LookItem.LookTelevision -> look.posterPath.orEmpty()
        is LookItem.LookPerson -> look.profilePath.orEmpty()
    }
}

private fun LookItem.mediaTypeLabel(): String {
    return when (this) {
        is LookItem.LookMovie -> "Movie"
        is LookItem.LookTelevision -> "TV"
        is LookItem.LookPerson -> "Person"
    }
}

private fun buildMovieSubtitle(look: Look): String? {
    return listOfNotNull(
        look.releaseDate?.takeIf { it.length >= 4 }?.take(4),
        look.average?.let { String.format(Locale.US, "TMDB %.1f", it) },
    ).takeIf { it.isNotEmpty() }?.joinToString(" • ")
}

private fun buildTvSubtitle(look: Look): String? {
    return listOfNotNull(
        look.firstAirDate?.takeIf { it.length >= 4 }?.take(4),
        look.average?.let { String.format(Locale.US, "TMDB %.1f", it) },
    ).takeIf { it.isNotEmpty() }?.joinToString(" • ")
}

private fun buildPersonSubtitle(knownFor: List<KnownFor>?): String? {
    val totalKnownFor = knownFor?.size ?: 0
    return if (totalKnownFor > 0) {
        "$totalKnownFor known for credits"
    } else {
        null
    }
}

private fun String?.orSearchFallback(): String {
    return if (this.isNullOrBlank()) {
        stringResourceSafe(R.string.search_unknown_title)
    } else {
        this
    }
}

private fun String?.orSearchDefaultDescription(): String {
    return if (this.isNullOrBlank()) {
        stringResourceSafe(R.string.search_default_description)
    } else {
        this
    }
}

private fun stringResourceSafe(resId: Int): String {
    return com.jim.moviecritics.MovieApplication.instance.getString(resId)
}
