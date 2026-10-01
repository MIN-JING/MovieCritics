package com.jim.moviecritics.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jim.moviecritics.data.*
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.*

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private val _homeItems = MutableLiveData<List<HomeItem>>()

    val homeItems: LiveData<List<HomeItem>>
        get() = _homeItems

    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    private val _navigateToDetail = MutableLiveData<Movie?>()

    val navigateToDetail: LiveData<Movie?>
        get() = _navigateToDetail

    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]$this")
        Logger.i("------------------------------------")

        getPopularMoviesResult()
    }

    fun getMovieFull(id: Int) {
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING
            val detailResult = getMovieDetail(index = 0, id = id)
            val creditResult = getMovieCredit(index = 1, id = id)
            _status.value = LoadApiStatus.DONE
            navigateToDetail(buildMovie(detailResult, creditResult))
        }
    }

    private fun navigateToDetail(movie: Movie) {
        _navigateToDetail.value = movie
    }

    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }

    private fun getPopularMoviesResult() {
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING
            val result = repository.getPopularMovies()
            _homeItems.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
        }
    }

    private suspend fun getMovieDetail(index: Int, id: Int): MovieDetailResult? {
        return withContext(Dispatchers.IO) {
            when (val result = repository.getMovieDetail(id)) {
                is Result.Success -> {
                    _error.postValue(null)
                    Logger.w("child $index result: ${result.data}")
                    result.data
                }
                is Result.Fail -> {
                    _error.postValue(result.error)
                    _status.postValue(LoadApiStatus.ERROR)
                    null
                }
                is Result.Error -> {
                    _error.postValue(result.exception.toString())
                    _status.postValue(LoadApiStatus.ERROR)
                    null
                }
                else -> {
                    _status.postValue(LoadApiStatus.ERROR)
                    null
                }
            }
        }
    }

    private suspend fun getMovieCredit(index: Int, id: Int): CreditResult? {
        return withContext(Dispatchers.IO) {
            when (val result = repository.getMovieCredit(id)) {
                is Result.Success -> {
                    _error.postValue(null)
                    Logger.w("child $index result: ${result.data}")
                    result.data
                }
                is Result.Fail -> {
                    _error.postValue(result.error)
                    _status.postValue(LoadApiStatus.ERROR)
                    null
                }
                is Result.Error -> {
                    _error.postValue(result.exception.toString())
                    _status.postValue(LoadApiStatus.ERROR)
                    null
                }
                else -> {
                    _status.postValue(LoadApiStatus.ERROR)
                    null
                }
            }
        }
    }
}
