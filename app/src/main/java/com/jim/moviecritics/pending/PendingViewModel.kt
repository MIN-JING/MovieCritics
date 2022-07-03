package com.jim.moviecritics.pending


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.jim.moviecritics.MovieApplication
import com.jim.moviecritics.R
import com.jim.moviecritics.data.*
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.*
import kotlin.math.roundToInt

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

    val leisurePending = MutableLiveData<Float>()

    val hitPending = MutableLiveData<Float>()

    val castPending = MutableLiveData<Float>()

    val musicPending = MutableLiveData<Float>()

    val storyPending = MutableLiveData<Float>()

    val score = Score()

//    val _score = MutableLiveData<Score>()

//    val score = MutableLiveData<Score>()
//        get() = _score


    private val _invalidScore = MutableLiveData<Int>()

    val invalidScore: LiveData<Int>
        get() = _invalidScore

//    private val _isFillScore = MutableLiveData<Boolean>()
//
//    val isFillScore: LiveData<Boolean>
//        get() = _isFillScore


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

//        coroutineScope.launch {
////            _user.value = getUserResult(isInitial = true, userID = "200001")
//            _isWatch.value = user.value?.watched?.contains(movie.value?.imdbID.toString())
//            _isLike.value = user.value?.liked?.contains(movie.value?.imdbID.toString())
//            _isWatchList.value = user.value?.watchlist?.contains(movie.value?.imdbID.toString())
//        }

    }

    fun takeDownUser(user: User) {
        _user.value = user
        Logger.i("Detail takeDownUser() = ${_user.value}")
    }

    fun initToggleAndScore() {
        _isWatch.value = user.value?.watched?.contains(movie.value?.imdbID.toString())
        _isLike.value = user.value?.liked?.contains(movie.value?.imdbID.toString())
        _isWatchList.value = user.value?.watchlist?.contains(movie.value?.imdbID.toString())

        score.imdbID = movie.value?.imdbID.toString()
        score.userID = user.value?.id.toString()
    }


//    private suspend fun getUserResult(isInitial: Boolean = false, userID: String): User? {
//
//        return withContext(Dispatchers.IO) {
//
//            if (isInitial) _status.postValue(LoadApiStatus.LOADING)
//
//            when (val result = applicationRepository.getUser(userID)) {
//                is Result.Success -> {
//                    _error.postValue(null)
//                    if (isInitial) _status.postValue(LoadApiStatus.DONE)
//                    result.data
//                }
//                is Result.Fail -> {
//                    _error.postValue(result.error)
//                    if (isInitial) _status.postValue(LoadApiStatus.ERROR)
//                    null
//                }
//                is Result.Error -> {
//                    _error.postValue(result.exception.toString())
//                    if (isInitial) _status.postValue(LoadApiStatus.ERROR)
//                    null
//                }
//                else -> {
//                    _error.postValue(Util.getString(R.string.you_know_nothing))
//                    if (isInitial) _status.postValue(LoadApiStatus.ERROR)
//                    null
//                }
//            }
//        }
//    }

    fun onClickWatch(imdbID: String, userID: String) {
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

    fun onClickLike(imdbID: String, userID: String) {
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

    fun onClickWatchList(imdbID: String, userID: String) {
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


    private fun pushScore(score: Score) {

        coroutineScope.launch {

//            score.imdbID = movie.value?.imdbID.toString()
//
//            user.value?.let {
//                score.userID = it.id
//            }

            score.createdTime = Timestamp.now()

            _status.value = LoadApiStatus.LOADING

            when (val result = applicationRepository.pushScore(score)) {
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
    }

    private fun prepareScore() {
        Logger.i("prepareScore()")
//        when {
////            leisurePending.value?.isNaN() == true -> _invalidScore.value = INVALID_FORMAT_LEISURE_EMPTY
////            hitPending.value?.isNaN() == true -> _invalidScore.value = INVALID_FORMAT_HIT_EMPTY
////            castPending.value?.isNaN() == true -> _invalidScore.value = INVALID_FORMAT_CAST_EMPTY
////            musicPending.value?.isNaN() == true -> _invalidScore.value = INVALID_FORMAT_MUSIC_EMPTY
////            storyPending.value?.isNaN() == true -> _invalidScore.value = INVALID_FORMAT_STORY_EMPTY
//
//            score.value?.leisure == null -> _invalidScore.value = INVALID_FORMAT_LEISURE_EMPTY
//            score.value?.hit == null -> _invalidScore.value = INVALID_FORMAT_HIT_EMPTY
//            score.value?.cast == null -> _invalidScore.value = INVALID_FORMAT_CAST_EMPTY
//            score.value?.music == null -> _invalidScore.value = INVALID_FORMAT_MUSIC_EMPTY
//            score.value?.story == null -> _invalidScore.value = INVALID_FORMAT_STORY_EMPTY
//
//            score.value?.leisure != null
//                && score.value?.hit != null
//                && score.value?.cast != null
//                && score.value?.music != null
//                && score.value?.story != null -> {
//                    _score.value?.average = (((score.value!!.leisure + score.value!!.hit + score.value!!.cast + score.value!!.music + score.value!!.story) * 10).roundToInt() / 50).toFloat()
//                    Logger.i("score.average = ${score.value!!.average}" )
//                    _score.value = _score.value
//
//                    score.value?.let {
//                        pushScore(it)
//                    }
//                    _invalidScore.value = SCORE_IS_FILLED
//                }
//
//            else -> _invalidScore.value = NO_ONE_KNOWS
//        }

        if (leisurePending.value != null && hitPending.value != null && castPending.value != null && musicPending.value != null && storyPending.value != null) {
            Logger.i("五個分數都不是 null")
            score.leisure = leisurePending.value!!
            score.hit = hitPending.value!!
            score.cast = castPending.value!!
            score.music = musicPending.value!!
            score.story = storyPending.value!!
            score.average = (((leisurePending.value!! + hitPending.value!! + castPending.value!! + musicPending.value!! + storyPending.value!!) * 10).roundToInt() / 50).toFloat()
            Logger.i("score.average = ${score.average}" )
            Logger.i("score = $score" )
            pushScore(score)
            _invalidScore.value = SCORE_IS_FILLED
        } else {
            Logger.i("五個分數有一個是 null")
        }

    }


    fun leave() {
        prepareScore()
        _leave.value = true

//        if (leisurePending.value!! >= 0.5F
//            && hitPending.value!! >= 0.5F
//            && castPending.value!! >= 0.5
//            && musicPending.value!! >= 0.5
//            && storyPending.value!! >= 0.5) {
//
//            leisurePending.value?.let {
//                score.leisure = it
//            }
//
//            hitPending.value?.let {
//                score.hit = it
//            }
//
//            castPending.value?.let {
//                score.cast = it
//            }
//
//            musicPending.value?.let {
//                score.music = it
//            }
//
//            storyPending. value?.let {
//                score.story = it
//            }
//
////        score.average = (score.leisure + score.hit + score.cast + score.music + score.story) / 5
//            score.average = (((score.leisure + score.hit + score.cast + score.music + score.story) * 10).roundToInt() / 50).toFloat()
//            Logger.i("score.average = ${score.average}" )
//
////            _isFillScore.value = true
//            pushScore()
//            _leave.value = true
//
//        } else {
////            _isFillScore.value = false
//            _leave.value = false
//        }
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