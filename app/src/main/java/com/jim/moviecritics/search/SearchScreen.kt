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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.Timestamp
import com.jim.moviecritics.R
import com.jim.moviecritics.data.*
import com.jim.moviecritics.data.source.Repository
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

@Preview(showBackground = true, name = "Search Screen Preview")
@Composable
fun SearchScreenPreview() {

    // Sample data for preview
    val sampleLookItems = listOf(
        LookItem.LookMovie(Look(mediaType = "movie", title = "Movie 1", overview = "Overview 1", posterPath = "")),
        LookItem.LookTelevision(Look(mediaType = "tv", title = "TV Show 1", overview = "Overview 2", posterPath = "")),
        LookItem.LookPerson(Look(mediaType = "person", title = "Person 1", overview = "Overview 3", profilePath = ""))
    )

    val fakeRepository = object : Repository {
        override suspend fun getPopularMovies(): Result<List<HomeItem>> {
            TODO("Not yet implemented")
        }

        override suspend fun getMovieDetail(id: Int): Result<MovieDetailResult> {
            TODO("Not yet implemented")
        }

        override suspend fun getMovieCredit(id: Int): Result<CreditResult> {
            TODO("Not yet implemented")
        }

        override suspend fun getSearchMulti(queryKey: String): Result<List<LookItem>> {
            return Result.Success(sampleLookItems)
        }

        override suspend fun getFind(imdbID: String): Result<FindResult> {
            TODO("Not yet implemented")
        }

        override fun getLiveWatchList(imdbID: String, userID: String): MutableLiveData<Watch> {
            TODO("Not yet implemented")
        }

        override fun getLiveWatchListByUser(userID: String): MutableLiveData<List<Watch>> {
            TODO("Not yet implemented")
        }

        override suspend fun pushMultiWatchListExpiration(
            imdbID: String,
            userID: String,
            expiration: Timestamp
        ): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun pushSingleWatchListExpiration(watch: Watch): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun getScores(imdbID: String): Result<List<Score>> {
            TODO("Not yet implemented")
        }

        override suspend fun getScore(imdbID: String, userID: String): Result<Score> {
            TODO("Not yet implemented")
        }

        override fun getLiveScore(imdbID: String, userID: String): MutableLiveData<Score> {
            TODO("Not yet implemented")
        }

        override suspend fun pushUserInfo(user: User): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun getUserByToken(token: String): Result<User> {
            TODO("Not yet implemented")
        }

        override suspend fun getUserById(id: String): Result<User?> {
            TODO("Not yet implemented")
        }

        override suspend fun getUsersByIdList(idList: List<String>): Result<List<User>> {
            TODO("Not yet implemented")
        }

        override suspend fun getComments(imdbID: String): Result<List<Comment>> {
            TODO("Not yet implemented")
        }

        override fun getLiveComments(imdbID: String): MutableLiveData<List<Comment>> {
            TODO("Not yet implemented")
        }

        override fun getLivePersonalComments(userID: String): MutableLiveData<List<Comment>> {
            TODO("Not yet implemented")
        }

        override fun getLiveCommentsExcludeBlocks(
            imdbID: String,
            blocks: List<String>
        ): MutableLiveData<List<Comment>> {
            TODO("Not yet implemented")
        }

        override suspend fun pushComment(comment: Comment): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun delete(comment: Comment): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override fun getLivePersonalFavorites(userID: String): MutableLiveData<List<String>> {
            TODO("Not yet implemented")
        }

        override suspend fun pushWatchedMovie(imdbID: String, userID: String): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun removeWatchedMovie(imdbID: String, userID: String): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun pushLikedMovie(imdbID: String, userID: String): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun removeLikedMovie(imdbID: String, userID: String): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun pushWatchlistMovie(watch: Watch): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun removeWatchlistMovie(imdbID: String, userID: String): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun pushScore(score: Score): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun pushReport(report: Report): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun pushBlockUser(userID: String, blockedID: String): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun pushPopularMovies(trends: List<Trend>): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun pushMockComment(): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun pushMockScore(): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun pushMockUser(): Result<Boolean> {
            TODO("Not yet implemented")
        }
    }

    val viewModel = SearchViewModel(fakeRepository)
    val state = rememberSearchState(
        searchResults = sampleLookItems,
        searching = false
    )

    LaunchedEffect(Unit) {
//        viewModel._lookItems.value = sampleLookItems
    }

    MaterialTheme {
        SearchScreen(
            viewModel = viewModel,
            navigateToDetail = { /* provide navigation logic */ },
            state = state
        )
    }
}