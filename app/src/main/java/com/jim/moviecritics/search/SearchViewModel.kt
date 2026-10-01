package com.jim.moviecritics.search

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jim.moviecritics.R
import com.jim.moviecritics.data.CreditResult
import com.jim.moviecritics.data.LookItem
import com.jim.moviecritics.data.Movie
import com.jim.moviecritics.data.MovieDetailResult
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: Repository) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SearchUiState(recentSearches = SearchRecentStore.load())
    )
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _navigateToDetail = MutableLiveData<Movie?>()
    val navigateToDetail: LiveData<Movie?>
        get() = _navigateToDetail

    private val _userMessage = MutableLiveData<String?>()
    val userMessage: LiveData<String?>
        get() = _userMessage

    private var searchJob: Job? = null

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]$this")
        Logger.i("------------------------------------")
    }

    fun onQueryChange(query: TextFieldValue) {
        _uiState.update {
            it.copy(
                query = query,
                errorMessage = null,
            )
        }

        searchJob?.cancel()

        if (query.text.isBlank()) {
            _uiState.update {
                it.copy(
                    searching = false,
                    hasSearched = false,
                    rawResults = emptyList(),
                    errorMessage = null,
                )
            }
            return
        }

        searchJob = viewModelScope.launch {
            delay(400)
            performSearch(query.text.trim())
        }
    }

    fun onSearchFocusChange(focused: Boolean) {
        _uiState.update { it.copy(focused = focused) }
    }

    fun onClearQuery() {
        searchJob?.cancel()
        _uiState.update {
            it.copy(
                query = TextFieldValue(""),
                searching = false,
                hasSearched = false,
                rawResults = emptyList(),
                errorMessage = null,
                selectedFilter = SearchFilter.ALL,
            )
        }
    }

    fun onFilterSelected(filter: SearchFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun onSuggestionSelected(query: String) {
        val normalizedQuery = query.trim()

        if (normalizedQuery.isBlank()) {
            return
        }

        _uiState.update {
            it.copy(
                query = TextFieldValue(normalizedQuery),
                focused = false,
                errorMessage = null,
            )
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            performSearch(normalizedQuery)
        }
    }

    fun retrySearch() {
        val currentQuery = uiState.value.query.text.trim()

        if (currentQuery.isBlank()) {
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            performSearch(currentQuery)
        }
    }

    fun onResultSelected(item: LookItem) {
        when (item) {
            is LookItem.LookMovie -> openMovieDetail(item.id)
            is LookItem.LookTelevision -> {
                _userMessage.value = Util.getString(R.string.search_tv_not_supported)
            }
            is LookItem.LookPerson -> {
                _userMessage.value = Util.getString(R.string.search_person_not_supported)
            }
        }
    }

    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }

    fun onUserMessageShown() {
        _userMessage.value = null
    }

    private suspend fun performSearch(query: String) {
        _uiState.update {
            it.copy(
                searching = true,
                errorMessage = null,
            )
        }

        when (val result = repository.getSearchMulti(query)) {
            is Result.Success -> {
                _uiState.update {
                    it.copy(
                        searching = false,
                        hasSearched = true,
                        rawResults = result.data,
                        recentSearches = SearchRecentStore.save(query),
                        errorMessage = null,
                    )
                }
            }
            is Result.Fail -> {
                _uiState.update {
                    it.copy(
                        searching = false,
                        hasSearched = true,
                        rawResults = emptyList(),
                        errorMessage = result.error,
                    )
                }
            }
            is Result.Error -> {
                _uiState.update {
                    it.copy(
                        searching = false,
                        hasSearched = true,
                        rawResults = emptyList(),
                        errorMessage = result.exception.localizedMessage
                            ?: Util.getString(R.string.you_know_nothing),
                    )
                }
            }
            else -> {
                _uiState.update {
                    it.copy(
                        searching = false,
                        hasSearched = true,
                        rawResults = emptyList(),
                        errorMessage = Util.getString(R.string.you_know_nothing),
                    )
                }
            }
        }
    }

    private fun openMovieDetail(movieId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(searching = true, errorMessage = null) }

            val detailDeferred = async { repository.getMovieDetail(movieId) }
            val creditDeferred = async { repository.getMovieCredit(movieId) }

            val movie = Movie()
            val detailLoaded = applyMovieDetail(movie, detailDeferred.await())
            val creditLoaded = applyMovieCredit(movie, creditDeferred.await())

            _uiState.update { it.copy(searching = false) }

            if (detailLoaded && creditLoaded) {
                _navigateToDetail.value = movie
            } else if (_userMessage.value == null) {
                _userMessage.value = Util.getString(R.string.search_open_detail_failed)
            }
        }
    }

    private fun applyMovieDetail(movie: Movie, result: Result<MovieDetailResult>): Boolean {
        return when (result) {
            is Result.Success -> {
                val detail = result.data
                movie.id = detail.id
                movie.imdbID = detail.imdbID
                movie.awards = null
                movie.country = null
                movie.genres = detail.genres
                movie.overview = detail.overview
                movie.posterUri = detail.posterUri?.let { "https://image.tmdb.org/t/p/w185$it" }
                movie.released = detail.releaseDate
                movie.runtime = detail.runtime
                movie.revenue = detail.revenue
                movie.salesTaiwan = null
                movie.title = detail.title
                movie.ratings = emptyList()
                movie.voteAverage = detail.average / 2
                movie.trailerUri = detail.videos.results
                    ?.maxByOrNull { it.published }
                    ?.key
                    ?.let { "https://www.youtube.com/watch?v=$it" }
                true
            }
            is Result.Fail -> {
                _userMessage.value = result.error
                false
            }
            is Result.Error -> {
                _userMessage.value = result.exception.localizedMessage
                    ?: Util.getString(R.string.search_open_detail_failed)
                false
            }
            else -> false
        }
    }

    private fun applyMovieCredit(movie: Movie, result: Result<CreditResult>): Boolean {
        return when (result) {
            is Result.Success -> {
                val credit = result.data
                credit.casts.forEach { cast ->
                    cast.profilePath = cast.profilePath?.let { "https://image.tmdb.org/t/p/w185$it" }
                }
                movie.casts = credit.casts
                movie.crews = credit.crews
                movie.writing.clear()
                credit.crews.forEach { crew ->
                    when (crew.job) {
                        "Director" -> movie.director = crew.name
                        "Story" -> movie.writing.add(crew.name)
                    }
                }
                true
            }
            is Result.Fail -> {
                _userMessage.value = result.error
                false
            }
            is Result.Error -> {
                _userMessage.value = result.exception.localizedMessage
                    ?: Util.getString(R.string.search_open_detail_failed)
                false
            }
            else -> false
        }
    }
}
