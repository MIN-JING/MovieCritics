package com.jim.moviecritics.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jim.moviecritics.R
import com.jim.moviecritics.data.Cast
import com.jim.moviecritics.data.Comment
import com.jim.moviecritics.data.Movie
import com.jim.moviecritics.data.Score
import com.jim.moviecritics.data.User
import com.jim.moviecritics.ui.theme.MovieCriticsTheme
import com.jim.moviecritics.util.GlideImage

@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetailContent(
        uiState = uiState,
        onBack = viewModel::leave,
        onPendingClick = viewModel::navigateToPending,
        onTrailerClick = viewModel::navigateToTrailer,
        onUserClick = viewModel::navigateToUserInfo,
        onReportClick = viewModel::navigateToReport,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailContent(
    uiState: DetailUiState,
    onBack: () -> Unit,
    onPendingClick: () -> Unit,
    onTrailerClick: () -> Unit,
    onUserClick: (User) -> Unit,
    onReportClick: (Comment) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.button_detail_back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onPendingClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_pending),
                            contentDescription = stringResource(R.string.button_detail_pending),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
        ) {
            item {
                DetailHeader(
                    uiState = uiState,
                    onTrailerClick = onTrailerClick,
                )
            }
            item {
                SectionTitle(text = stringResource(R.string.text_detail_ratings))
                DetailRatings(uiState = uiState)
            }
            if (uiState.movie.casts.isNotEmpty()) {
                item {
                    SectionTitle(text = stringResource(R.string.text_detail_cast))
                    CastRow(casts = uiState.movie.casts)
                }
            }
            item {
                SectionTitle(text = stringResource(R.string.text_detail_review))
                if (uiState.comments.isEmpty()) {
                    Text(
                        text = stringResource(R.string.text_detail_no_reviews),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            items(uiState.comments) { comment ->
                ReviewItem(
                    comment = comment,
                    user = uiState.usersById[comment.userID],
                    onUserClick = onUserClick,
                    onReportClick = onReportClick,
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun DetailHeader(
    uiState: DetailUiState,
    onTrailerClick: () -> Unit,
) {
    val movie = uiState.movie
    val posterDescription = stringResource(R.string.image_detail_poster)

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                if (movie.released.isNotBlank()) {
                    Text(
                        text = movie.released,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (!movie.director.isNullOrBlank()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.text_detail_directed_by),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = movie.director.orEmpty(),
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button(
                        onClick = onTrailerClick,
                        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_play_arrow),
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                        )
                        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                        Text(text = stringResource(R.string.button_detail_trailer))
                    }
                    movie.runtime?.takeIf { it > 0 }?.let { runtime ->
                        Text(
                            text = stringResource(R.string.text_detail_runtime, runtime),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            GlideImage(
                imageUrl = movie.posterUri.orEmpty(),
                modifier = Modifier
                    .size(width = 120.dp, height = 160.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .semantics { contentDescription = posterDescription },
            )
        }

        if (!movie.overview.isNullOrBlank()) {
            Text(
                text = movie.overview.orEmpty(),
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
    Text(
        text = text,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
        style = MaterialTheme.typography.titleLarge,
    )
}

@Composable
private fun DetailRatings(uiState: DetailUiState) {
    val tmdbColor = MaterialTheme.colorScheme.secondary
    val userColor = MaterialTheme.colorScheme.primary
    val userRatings = uiState.userRatings
    val series = buildList {
        add(RadarSeries(values = uiState.tmdbRatings, color = tmdbColor))
        userRatings?.let { add(RadarSeries(values = it, color = userColor)) }
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        RatingRadarChart(
            labels = listOf(
                stringResource(R.string.text_pending_leisure),
                stringResource(R.string.text_pending_hit),
                stringResource(R.string.text_pending_cast),
                stringResource(R.string.text_pending_music),
                stringResource(R.string.text_pending_story),
            ),
            series = series,
            maxValue = DetailUiState.MAX_RATING,
        )
        LegendItem(
            color = tmdbColor,
            text = stringResource(R.string.text_detail_tmdb_rating, uiState.movie.voteAverage),
        )
        if (userRatings != null) {
            LegendItem(
                color = userColor,
                text = stringResource(R.string.text_detail_your_rating, userRatings.average()),
            )
        } else {
            Text(
                text = stringResource(R.string.text_detail_not_rated),
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, text: String) {
    Row(
        modifier = Modifier.padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun CastRow(casts: List<Cast>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(casts) { cast ->
            CastItem(cast = cast)
        }
    }
}

@Composable
private fun CastItem(cast: Cast) {
    val profileDescription = stringResource(R.string.image_cast)

    Column(modifier = Modifier.width(88.dp)) {
        GlideImage(
            imageUrl = cast.profilePath.orEmpty(),
            modifier = Modifier
                .size(width = 88.dp, height = 112.dp)
                .clip(RoundedCornerShape(8.dp))
                .semantics { contentDescription = profileDescription },
        )
        Text(
            text = cast.name,
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = cast.character,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ReviewItem(
    comment: Comment,
    user: User?,
    onUserClick: (User) -> Unit,
    onReportClick: (Comment) -> Unit,
) {
    val userPictureDescription = stringResource(R.string.image_detail_item_review_user_pic)

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(enabled = user != null) { user?.let(onUserClick) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GlideImage(
                    imageUrl = user?.pictureUri.orEmpty(),
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .semantics { contentDescription = userPictureDescription },
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = user?.name.orEmpty(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(onClick = { onReportClick(comment) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_flag),
                    contentDescription = stringResource(
                        R.string.image_detail_item_review_report_description
                    ),
                    modifier = Modifier.size(24.dp),
                )
            }
        }
        Text(
            text = comment.content,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 6,
            overflow = TextOverflow.Ellipsis,
        )
        HorizontalDivider(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
    }
}

@Preview(showBackground = true, name = "Detail Screen Preview")
@Composable
private fun DetailScreenPreview() {
    MovieCriticsTheme {
        DetailContent(
            uiState = DetailUiState(
                movie = Movie(
                    title = "Titanic",
                    released = "1997-12-19",
                    director = "James Cameron",
                    runtime = 194,
                    overview = "101-year-old Rose DeWitt Bukater tells the story of her life " +
                        "aboard the Titanic.",
                    voteAverage = 3.9F
                ),
                userScore = Score(
                    id = "1", leisure = 4F, hit = 5F, cast = 4F, music = 5F, story = 3F
                ),
                comments = listOf(Comment(id = "1", userID = "u1", content = "A classic.")),
                usersById = mapOf("u1" to User(id = "u1", name = "Jim")),
            ),
            onBack = {},
            onPendingClick = {},
            onTrailerClick = {},
            onUserClick = {},
            onReportClick = {},
        )
    }
}
