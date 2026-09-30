package com.jim.moviecritics.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jim.moviecritics.R
import com.jim.moviecritics.data.LookItem
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.buildMovie
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: Repository) : ViewModel() {

    companion object {
        const val SEARCH_DEBOUNCE_MILLIS = 500L
    }

    // Kept as Compose state rather than a StateFlow so the text field is updated synchronously
    var query by mutableStateOf("")
        private set

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var detailJob: Job? = null

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]$this")
        Logger.i("------------------------------------")
    }

    fun onQueryChange(newQuery: String) {
        query = newQuery
        searchJob?.cancel()

        val queryKey = newQuery.trim()
        if (queryKey.isEmpty()) {
            _uiState.update { it.copy(results = emptyList(), searching = false, error = null) }
            return
        }

        _uiState.update { it.copy(searching = true) }
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MILLIS)
            val result = repository.getSearchMulti(queryKey)
            // The data source catches every exception, including cancellation
            ensureActive()

            _uiState.update { state ->
                when (result) {
                    is Result.Success -> state.copy(
                        results = result.data,
                        searching = false,
                        error = null
                    )
                    is Result.Fail -> state.copy(
                        results = emptyList(),
                        searching = false,
                        error = result.error
                    )
                    is Result.Error -> state.copy(
                        results = emptyList(),
                        searching = false,
                        error = result.exception.toString()
                    )
                    else -> state.copy(
                        results = emptyList(),
                        searching = false,
                        error = Util.getString(R.string.you_know_nothing)
                    )
                }
            }
        }
    }

    fun onItemClick(item: LookItem) {
        when (item) {
            is LookItem.LookMovie -> loadMovieDetail(item.id)
            is LookItem.LookTelevision, is LookItem.LookPerson ->
                _uiState.update { it.copy(userMessage = R.string.search_detail_not_supported) }
        }
    }

    private fun loadMovieDetail(id: Int) {
        if (detailJob?.isActive == true) return

        detailJob = viewModelScope.launch {
            _uiState.update { it.copy(loadingDetail = true) }

            val detailDeferred = async { repository.getMovieDetail(id) }
            val creditDeferred = async { repository.getMovieCredit(id) }
            val detailResult = detailDeferred.await()
            val creditResult = creditDeferred.await()
            ensureActive()

            _uiState.update { state ->
                if (detailResult is Result.Success) {
                    state.copy(
                        loadingDetail = false,
                        navigateToDetail = buildMovie(
                            detailResult.data,
                            (creditResult as? Result.Success)?.data
                        )
                    )
                } else {
                    Logger.w("loadMovieDetail id=$id failed: $detailResult")
                    state.copy(
                        loadingDetail = false,
                        userMessage = R.string.search_detail_load_failed
                    )
                }
            }
        }
    }

    fun onDetailNavigated() {
        _uiState.update { it.copy(navigateToDetail = null) }
    }

    fun onUserMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
