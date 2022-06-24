package com.jim.moviecritics.pending

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jim.moviecritics.MovieApplication
import com.jim.moviecritics.R
import com.jim.moviecritics.data.Movie
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.User
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util
import kotlinx.coroutines.*

class PendingViewModel(
    private val applicationRepository: ApplicationRepository,
    private val arguments: Movie
) : ViewModel() {

    private val _movie = MutableLiveData<Movie>().apply {
        value = arguments
    }

    val movie: LiveData<Movie>
        get() = _movie

    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    private val _isWatch = MutableLiveData<Boolean>()

    val isWatch: LiveData<Boolean>
        get() = _isWatch

    private val _isLike = MutableLiveData<Boolean>()

    val isLike: LiveData<Boolean>
        get() = _isLike

    private val _isWatchList = MutableLiveData<Boolean>()

    val isWatchList: LiveData<Boolean>
        get() = _isWatchList

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // Handle leave
    private val _leave = MutableLiveData<Boolean?>()

    val leave: LiveData<Boolean?>
        get() = _leave

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

        coroutineScope.launch {
            _user.value = getUserResult(isInitial = true, userID = 790926)
            _isWatch.value = user.value?.watched?.contains(movie.value?.imdbID.toString())
            _isLike.value = user.value?.liked?.contains(movie.value?.imdbID.toString())
            _isWatchList.value = user.value?.watchlist?.contains(movie.value?.imdbID.toString())
        }

    }


    private suspend fun getUserResult(isInitial: Boolean = false, userID: Long): User? {

        return withContext(Dispatchers.IO) {

            if (isInitial) _status.postValue(LoadApiStatus.LOADING)

            when (val result = applicationRepository.getUser(userID)) {
                is Result.Success -> {
                    _error.postValue(null)
                    if (isInitial) _status.postValue(LoadApiStatus.DONE)
                    result.data
                }
                is Result.Fail -> {
                    _error.postValue(result.error)
                    if (isInitial) _status.postValue(LoadApiStatus.ERROR)
                    null
                }
                is Result.Error -> {
                    _error.postValue(result.exception.toString())
                    if (isInitial) _status.postValue(LoadApiStatus.ERROR)
                    null
                }
                else -> {
                    _error.postValue(Util.getString(R.string.you_know_nothing))
                    if (isInitial) _status.postValue(LoadApiStatus.ERROR)
                    null
                }
            }
        }
    }

    fun onClickWatch(imdbID: String, userID: Long) {
        if (isWatch.value != true) {
            Logger.i("isWatch.value != true")
            Logger.i("user.value?.watched = ${user.value?.watched}")
            Logger.i("movie.value?.imdbID = ${movie.value?.imdbID}")
            _user.value?.watched?.add(imdbID)
            Logger.i("user.value?.watched add = ${user.value?.watched}")
            coroutineScope.launch {
                _status.value = LoadApiStatus.LOADING

                when (val result = applicationRepository.pushWatchedMovie(imdbID, userID)) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadApiStatus.DONE
                    }
                    is Result.Fail -> {
                        _error.value = result.error
                        _status.value = LoadApiStatus.ERROR
                    }
                    is Result.Error -> {
                        _error.value = result.exception.toString()
                        _status.value = LoadApiStatus.ERROR
                    }
                    else -> {
                        _error.value = MovieApplication.instance.getString(R.string.you_know_nothing)
                        _status.value = LoadApiStatus.ERROR
                    }
                }
            }
            _isWatch.value = true
        } else {
            Logger.i("isWatch.value != false")
            Logger.i("user.value?.watched = ${user.value?.watched}")
            Logger.i("movie.value?.imdbID = ${movie.value?.imdbID}")
            _user.value?.watched?.remove(imdbID)
            Logger.i("user.value?.watched remove = ${user.value?.watched}")
            coroutineScope.launch {
                _status.value = LoadApiStatus.LOADING

                when (val result = applicationRepository.removeWatchedMovie(imdbID, userID)) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadApiStatus.DONE
                    }
                    is Result.Fail -> {
                        _error.value = result.error
                        _status.value = LoadApiStatus.ERROR
                    }
                    is Result.Error -> {
                        _error.value = result.exception.toString()
                        _status.value = LoadApiStatus.ERROR
                    }
                    else -> {
                        _error.value = MovieApplication.instance.getString(R.string.you_know_nothing)
                        _status.value = LoadApiStatus.ERROR
                    }
                }
            }
            _isWatch.value = false
        }
    }

    fun onClickLike(imdbID: String, userID: Long) {
        if (isLike.value != true) {
            Logger.i("isLike.value != true")
            Logger.i("user.value?.liked = ${user.value?.liked}")
            Logger.i("movie.value?.imdbID = ${movie.value?.imdbID}")
            _user.value?.liked?.add(imdbID)
            Logger.i("user.value?.liked add = ${user.value?.liked}")
            coroutineScope.launch {
                _status.value = LoadApiStatus.LOADING

                when (val result = applicationRepository.pushLikedMovie(imdbID, userID)) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadApiStatus.DONE
                    }
                    is Result.Fail -> {
                        _error.value = result.error
                        _status.value = LoadApiStatus.ERROR
                    }
                    is Result.Error -> {
                        _error.value = result.exception.toString()
                        _status.value = LoadApiStatus.ERROR
                    }
                    else -> {
                        _error.value = MovieApplication.instance.getString(R.string.you_know_nothing)
                        _status.value = LoadApiStatus.ERROR
                    }
                }
            }
            _isLike.value = true
        } else {
            Logger.i("isLike.value != false")
            Logger.i("user.value?.liked = ${user.value?.liked}")
            Logger.i("movie.value?.imdbID = ${movie.value?.imdbID}")
            _user.value?.liked?.remove(imdbID)
            Logger.i("user.value?.liked remove = ${user.value?.liked}")
            coroutineScope.launch {
                _status.value = LoadApiStatus.LOADING

                when (val result = applicationRepository.removeLikedMovie(imdbID, userID)) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadApiStatus.DONE
                    }
                    is Result.Fail -> {
                        _error.value = result.error
                        _status.value = LoadApiStatus.ERROR
                    }
                    is Result.Error -> {
                        _error.value = result.exception.toString()
                        _status.value = LoadApiStatus.ERROR
                    }
                    else -> {
                        _error.value = MovieApplication.instance.getString(R.string.you_know_nothing)
                        _status.value = LoadApiStatus.ERROR
                    }
                }
            }
            _isLike.value = false
        }
    }

    fun onClickWatchList(imdbID: String, userID: Long) {
        if (isWatchList.value != true) {
            Logger.i("isWatchList.value != true")
            Logger.i("user.value?.watchlist = ${user.value?.watchlist}")
            Logger.i("movie.value?.imdbID = ${movie.value?.imdbID}")
            _user.value?.watchlist?.add(imdbID)
            Logger.i("user.value?.watchlist add = ${user.value?.watchlist}")
            coroutineScope.launch {
                _status.value = LoadApiStatus.LOADING

                when (val result = applicationRepository.pushWatchlistMovie(imdbID, userID)) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadApiStatus.DONE
                    }
                    is Result.Fail -> {
                        _error.value = result.error
                        _status.value = LoadApiStatus.ERROR
                    }
                    is Result.Error -> {
                        _error.value = result.exception.toString()
                        _status.value = LoadApiStatus.ERROR
                    }
                    else -> {
                        _error.value = MovieApplication.instance.getString(R.string.you_know_nothing)
                        _status.value = LoadApiStatus.ERROR
                    }
                }
            }
            _isWatchList.value = true
        } else {
            Logger.i("isWatchList.value != false")
            Logger.i("user.value?.watchlist = ${user.value?.watchlist}")
            Logger.i("movie.value?.imdbID = ${movie.value?.imdbID}")
            _user.value?.watchlist?.remove(imdbID)
            Logger.i("user.value?.watchlist remove = ${user.value?.watchlist}")
            coroutineScope.launch {
                _status.value = LoadApiStatus.LOADING

                when (val result = applicationRepository.removeWatchlistMovie(imdbID, userID)) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadApiStatus.DONE
                    }
                    is Result.Fail -> {
                        _error.value = result.error
                        _status.value = LoadApiStatus.ERROR
                    }
                    is Result.Error -> {
                        _error.value = result.exception.toString()
                        _status.value = LoadApiStatus.ERROR
                    }
                    else -> {
                        _error.value = MovieApplication.instance.getString(R.string.you_know_nothing)
                        _status.value = LoadApiStatus.ERROR
                    }
                }
            }
            _isWatchList.value = false
        }
    }


    fun leave() {
        _leave.value = true
    }

    fun onLeaveCompleted() {
        _leave.value = null
    }
}