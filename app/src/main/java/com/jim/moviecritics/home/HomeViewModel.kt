package com.jim.moviecritics.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jim.moviecritics.R
import com.jim.moviecritics.data.Movie
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.buildMovie
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _navigateToDetail = MutableLiveData<Movie?>()
    val navigateToDetail: LiveData<Movie?>
        get() = _navigateToDetail

    private val _userMessage = MutableLiveData<String?>()
    val userMessage: LiveData<String?>
        get() = _userMessage

    private var popularJob: Job? = null
    private var detailJob: Job? = null

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]$this")
        Logger.i("------------------------------------")

        loadPopularMovies()
    }

    fun retry() {
        loadPopularMovies()
    }

    fun onMovieSelected(movieId: Int) {
        if (detailJob?.isActive == true) return

        detailJob = viewModelScope.launch {
            _uiState.update { it.copy(openingDetail = true) }

            val detailDeferred = async { repository.getMovieDetail(movieId) }
            val creditDeferred = async { repository.getMovieCredit(movieId) }
            val detailResult = detailDeferred.await()
            val creditResult = creditDeferred.await()
            // The data source catches every exception, including cancellation
            ensureActive()

            _uiState.update { it.copy(openingDetail = false) }

            if (detailResult is Result.Success && creditResult is Result.Success) {
                _navigateToDetail.value = buildMovie(detailResult.data, creditResult.data)
            } else {
                _userMessage.value = detailResult.failureMessage()
                    ?: creditResult.failureMessage()
                    ?: Util.getString(R.string.home_open_detail_failed)
            }
        }
    }

    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }

    fun onUserMessageShown() {
        _userMessage.value = null
    }

    private fun loadPopularMovies() {
        if (popularJob?.isActive == true) return

        popularJob = viewModelScope.launch {
            _uiState.update { it.copy(loadingPopular = true, errorMessage = null) }

            val result = repository.getPopularMovies()
            // The data source catches every exception, including cancellation
            ensureActive()

            _uiState.update {
                when (result) {
                    is Result.Success -> it.copy(
                        loadingPopular = false,
                        homeItems = result.data,
                        errorMessage = null,
                    )
                    else -> it.copy(
                        loadingPopular = false,
                        errorMessage = result.failureMessage()
                            ?: Util.getString(R.string.you_know_nothing),
                    )
                }
            }
        }
    }

    private fun Result<*>.failureMessage(): String? = when (this) {
        is Result.Fail -> error
        is Result.Error -> exception.localizedMessage
        else -> null
    }
}
