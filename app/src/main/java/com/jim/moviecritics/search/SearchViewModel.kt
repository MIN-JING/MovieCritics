package com.jim.moviecritics.search

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jim.moviecritics.R
import com.jim.moviecritics.data.LookItem
import com.jim.moviecritics.data.Movie
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.buildMovie
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
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
    private var detailJob: Job? = null

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

        val result = repository.getSearchMulti(query)
        // The data source catches every exception, including cancellation
        currentCoroutineContext().ensureActive()

        when (result) {
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
        if (detailJob?.isActive == true) return

        detailJob = viewModelScope.launch {
            _uiState.update { it.copy(searching = true, errorMessage = null) }

            val detailDeferred = async { repository.getMovieDetail(movieId) }
            val creditDeferred = async { repository.getMovieCredit(movieId) }
            val detailResult = detailDeferred.await()
            val creditResult = creditDeferred.await()
            ensureActive()

            _uiState.update { it.copy(searching = false) }

            if (detailResult is Result.Success && creditResult is Result.Success) {
                _navigateToDetail.value = buildMovie(detailResult.data, creditResult.data)
            } else {
                _userMessage.value = detailResult.failureMessage()
                    ?: creditResult.failureMessage()
                    ?: Util.getString(R.string.search_open_detail_failed)
            }
        }
    }

    private fun Result<*>.failureMessage(): String? = when (this) {
        is Result.Fail -> error
        is Result.Error -> exception.localizedMessage
        else -> null
    }
}
