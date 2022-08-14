package com.jim.moviecritics.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.RadarEntry
import com.jim.moviecritics.data.*
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.login.UserManager
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.*

class DetailViewModel(
    private val repository: Repository,
    private val arguments: Movie
) : ViewModel() {

    private val _movie = MutableLiveData<Movie>().apply {
        value = arguments
    }

    val movie: LiveData<Movie>
        get() = _movie


    private var users = listOf<User>()

    // check user login status
    val isLoggedIn
        get() = UserManager.isLoggedIn

    var usersMap = mapOf<String, User>()

    var liveScore = MutableLiveData<Score>()

    var liveComments = MutableLiveData<List<Comment>>()

    lateinit var averageRatings: ArrayList<RadarEntry>

    var userRatings: ArrayList<RadarEntry> =
        arrayListOf(
            RadarEntry(0F),
            RadarEntry(0F),
            RadarEntry(0F),
            RadarEntry(0F),
            RadarEntry(0F)
        )

    private val _isUsersMapReady = MutableLiveData<Boolean>()

    val isUsersMapReady: LiveData<Boolean>
        get() = _isUsersMapReady

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave

    private val _navigateToPending = MutableLiveData<Movie?>()

    val navigateToPending: LiveData<Movie?>
        get() = _navigateToPending

    private val _navigateToReport = MutableLiveData<Comment?>()

    val navigateToReport: LiveData<Comment?>
        get() = _navigateToReport

    private val _navigateToUserInfo = MutableLiveData<User?>()

    val navigateToUserInfo: LiveData<User?>
        get() = _navigateToUserInfo

    private val _navigateToTrailer = MutableLiveData<Movie?>()

    val navigateToTrailer: LiveData<Movie?>
        get() = _navigateToTrailer

    private val _navigateToLogin = MutableLiveData<Boolean?>()

    val navigateToLogin: LiveData<Boolean?>
        get() = _navigateToLogin

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

        movie.value?.let { movie ->
            movie.imdbID?.let { imdbID ->
                UserManager.userID?.let { getLiveScore(imdbID = imdbID, it) }
                getLiveComments(imdbID = imdbID)
            }
            averageRatings = arrayListOf(
                RadarEntry(movie.voteAverage),
                RadarEntry(movie.voteAverage),
                RadarEntry(movie.voteAverage),
                RadarEntry(movie.voteAverage),
                RadarEntry(movie.voteAverage)
            )
        }
    }

    fun navigateToPending(movie: Movie) {
        _navigateToPending.value = movie
    }

    fun onPendingNavigated() {
        _navigateToPending.value = null
    }

    fun navigateToReport(comment: Comment) {
        _navigateToReport.value = comment
    }

    fun onReportNavigated() {
        _navigateToReport.value = null
    }

    fun navigateToUserInfo(user: User) {
        _navigateToUserInfo.value = user
    }

    fun onUserInfoNavigated() {
        _navigateToUserInfo.value = null
    }

    fun navigateToTrailer(movie: Movie) {
        _navigateToTrailer.value = movie
    }

    fun onTrailerNavigated() {
        _navigateToTrailer.value = null
    }

    fun navigateToLogin() {
        _navigateToLogin.value = true
    }

    fun onLoginNavigated() {
        _navigateToLogin.value = null
    }

    fun leave() {
        _leave.value = true
    }

    private fun getLiveScore(imdbID: String, userID: String) {
        liveScore.value = repository.getLiveScore(imdbID, userID).value
        liveScore = repository.getLiveScore(imdbID, userID)
    }

    private fun getLiveComments(imdbID: String) {
        _status.value = LoadApiStatus.LOADING
        liveComments = repository.getLiveComments(imdbID)
        _status.value = LoadApiStatus.DONE
    }

    fun getUsersResult(idList: List<String>) {
        coroutineScope.launch {
            val result = repository.getUsersByIdList(idList = idList)
            users = when (result) {
                is Result.Success -> {
                    _error.value = null
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    listOf()
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    listOf()
                }
                else -> {
                    _status.value = LoadApiStatus.ERROR
                    listOf()
                }
            }
            usersMap = users.associateBy(User::id)
            _isUsersMapReady.value = true
        }
    }

    fun setRadarEntry(score: Score) {
        userRatings = arrayListOf(
            RadarEntry(score.leisure),
            RadarEntry(score.hit),
            RadarEntry(score.cast),
            RadarEntry(score.music),
            RadarEntry(score.story)
        )
    }
}
