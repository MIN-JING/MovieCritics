package com.jim.moviecritics.pending

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.jim.moviecritics.data.*
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.login.UserManager
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.*

class PendingViewModel(
    private val repository: Repository,
    private val arguments: Movie
) : ViewModel() {

    private val _movie = MutableLiveData<Movie>().apply {
        value = arguments
    }

    val movie: LiveData<Movie>
        get() = _movie

    private val _user = MutableLiveData<User?>()

    val user: LiveData<User?>
        get() = _user


    val watch = Watch()

    var liveWatch = MutableLiveData<Watch>()

    private val _isWatch = MutableLiveData<Boolean>()

    val isWatch: LiveData<Boolean>
        get() = _isWatch

    private val _isLike = MutableLiveData<Boolean>()

    val isLike: LiveData<Boolean>
        get() = _isLike

    private val _isWatchList = MutableLiveData<Boolean>()

    val isWatchList: LiveData<Boolean>
        get() = _isWatchList

    val leisurePending = MutableLiveData<Float>()

    val hitPending = MutableLiveData<Float>()

    val castPending = MutableLiveData<Float>()

    val musicPending = MutableLiveData<Float>()

    val storyPending = MutableLiveData<Float>()

    private val score = Score()

    private val _invalidScore = MutableLiveData<Int>()

    val invalidScore: LiveData<Int>
        get() = _invalidScore

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

    private val _navigateToReview = MutableLiveData<Movie?>()

    val navigateToReview: LiveData<Movie?>
        get() = _navigateToReview

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

        initScoreAndWatch()
        setButtonStatus()
    }

    private fun initScoreAndWatch() {
        movie.value?.imdbID?.let { imdbID ->
            score.imdbID = imdbID
            watch.imdbID = imdbID
            UserManager.userID?.let { userID ->
                getLiveWatchListResult(imdbID = imdbID, userID = userID)
                score.userID = userID
                watch.userID = userID
            }
        }
    }

    private fun setButtonStatus() {
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING
            val userResult = UserManager.userID?.let { getUserByID(it) }
            _user.value = userResult
            movie.value?.imdbID?.let { imdbID ->
                Logger.i("setButtonStatus() user.value = ${user.value}")
                _isWatch.value = user.value?.watched?.contains(imdbID)
                _isLike.value = user.value?.liked?.contains(imdbID)
            }
            _status.value = LoadApiStatus.DONE
        }
    }

    fun onClickWatch(imdbID: String, userID: String) {
        if (isWatch.value != true) {
            Logger.i("isWatch.value != true")
            Logger.i("user.value?.watched = ${user.value?.watched}")
            Logger.i("movie.value?.imdbID = ${movie.value?.imdbID}")
            user.value?.watched?.add(imdbID)
            Logger.i("user.value?.watched add = ${user.value?.watched}")
            coroutineScope.launch {
                when (val result = repository.pushWatchedMovie(imdbID, userID)) {
                    is Result.Success -> {
                        _error.value = null
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
                        _status.value = LoadApiStatus.ERROR
                    }
                }
            }
            _isWatch.value = true
        } else {
            Logger.i("isWatch.value != false")
            Logger.i("user.value?.watched = ${user.value?.watched}")
            Logger.i("movie.value?.imdbID = ${movie.value?.imdbID}")
            user.value?.watched?.remove(imdbID)
            Logger.i("user.value?.watched remove = ${user.value?.watched}")
            coroutineScope.launch {
                when (val result = repository.removeWatchedMovie(imdbID, userID)) {
                    is Result.Success -> {
                        _error.value = null
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
                        _status.value = LoadApiStatus.ERROR
                    }
                }
            }
            _isWatch.value = false
        }
    }

    fun onClickLike(imdbID: String, userID: String) {
        if (isLike.value != true) {
            Logger.i("isLike.value != true")
            Logger.i("user.value?.liked = ${user.value?.liked}")
            Logger.i("movie.value?.imdbID = ${movie.value?.imdbID}")
            user.value?.liked?.add(imdbID)
            Logger.i("user.value?.liked add = ${user.value?.liked}")
            coroutineScope.launch {
                when (val result = repository.pushLikedMovie(imdbID, userID)) {
                    is Result.Success -> {
                        _error.value = null
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
                        _status.value = LoadApiStatus.ERROR
                    }
                }
            }
            _isLike.value = true
        } else {
            Logger.i("isLike.value != false")
            Logger.i("user.value?.liked = ${user.value?.liked}")
            Logger.i("movie.value?.imdbID = ${movie.value?.imdbID}")
            user.value?.liked?.remove(imdbID)
            Logger.i("user.value?.liked remove = ${user.value?.liked}")
            coroutineScope.launch {
                when (val result = repository.removeLikedMovie(imdbID, userID)) {
                    is Result.Success -> {
                        _error.value = null
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
                        _status.value = LoadApiStatus.ERROR
                    }
                }
            }
            _isLike.value = false
        }
    }

    fun onClickWatchList() {
        if (isWatchList.value != true) {
            coroutineScope.launch {
                when (val result = repository.pushWatchlistMovie(watch)) {
                    is Result.Success -> {
                        _error.value = null
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
                        _status.value = LoadApiStatus.ERROR
                    }
                }
            }
            _isWatchList.value = true
        } else {
            coroutineScope.launch {
                when (
                    val result = repository.removeWatchlistMovie(
                        imdbID = watch.imdbID, userID = watch.userID
                    )
                ) {
                    is Result.Success -> {
                        _error.value = null
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
                        _status.value = LoadApiStatus.ERROR
                    }
                }
            }
            _isWatchList.value = false
        }
    }

    private fun pushScore(score: Score) {
        coroutineScope.launch {
            score.createdTime = Timestamp.now()
            _status.value = LoadApiStatus.LOADING
            when (val result = repository.pushScore(score)) {
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
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    private fun prepareScore() {
        if (leisurePending.value != null &&
            hitPending.value != null &&
            castPending.value != null &&
            musicPending.value != null &&
            storyPending.value != null
        ) {
            Logger.i("五個分數都不是 null")
            score.leisure = leisurePending.value!!
            score.hit = hitPending.value!!
            score.cast = castPending.value!!
            score.music = musicPending.value!!
            score.story = storyPending.value!!
            score.average = (
                (leisurePending.value!! +
                    hitPending.value!! +
                    castPending.value!! +
                    musicPending.value!! +
                    storyPending.value!!) / 5
            )
            Logger.i("score.average = ${score.average}")
            Logger.i("score = $score")
            pushScore(score)
            _invalidScore.value = SCORE_IS_FILLED
        } else {
            Logger.i("五個分數有一個是 null")
            when {
                leisurePending.value == null -> _invalidScore.value = INVALID_FORMAT_LEISURE_EMPTY
                hitPending.value == null -> _invalidScore.value = INVALID_FORMAT_HIT_EMPTY
                castPending.value == null -> _invalidScore.value = INVALID_FORMAT_CAST_EMPTY
                musicPending.value == null -> _invalidScore.value = INVALID_FORMAT_MUSIC_EMPTY
                storyPending.value == null -> _invalidScore.value = INVALID_FORMAT_STORY_EMPTY
                else -> _invalidScore.value = NO_ONE_KNOWS
            }
        }
    }

    private fun getLiveWatchListResult(imdbID: String, userID: String) {
        liveWatch = repository.getLiveWatchList(imdbID, userID)
    }

    fun isWatchListEqualFalse() {
        _isWatchList.value = false
    }

    fun isWatchListEqualTrue() {
        _isWatchList.value = true
    }

    fun leave() {
        prepareScore()
        _leave.value = true
    }

    fun onLeaveCompleted() {
        _leave.value = null
    }

    fun navigateToReview(movie: Movie) {
        _navigateToReview.value = movie
    }

    fun onReviewNavigated() {
        _navigateToReview.value = null
    }

    fun share(): Intent {
        val share = Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                type = "*/*"
                putExtra(Intent.EXTRA_TEXT,
                    "https://www.themoviedb.org/movie/${movie.value?.id}")
                putExtra(Intent.EXTRA_TITLE, movie.value?.title)
                val uri = Uri.parse(movie.value?.posterUri)
                Logger.i("share uri = $uri")
                clipData = ClipData.newRawUri("", uri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            },
            null
        )
        return share
    }

    private suspend fun getUserByID(userID: String) : User? {
        return withContext(Dispatchers.IO) {
            when (val result = repository.getUserById(userID)) {
                is Result.Success -> {
                    _error.postValue(null)
                    Logger.w("child result: ${result.data}")
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

    companion object {
        const val INVALID_FORMAT_LEISURE_EMPTY = 0x11
        const val INVALID_FORMAT_HIT_EMPTY = 0x12
        const val INVALID_FORMAT_CAST_EMPTY = 0x13
        const val INVALID_FORMAT_MUSIC_EMPTY = 0x14
        const val INVALID_FORMAT_STORY_EMPTY = 0x15
        const val NO_ONE_KNOWS = 0x21
        const val SCORE_IS_FILLED = 0x31
    }
}