package com.jim.moviecritics.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.jim.moviecritics.data.Comment
import com.jim.moviecritics.data.Movie
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.User
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.login.UserManager
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: Repository,
    private val arguments: Movie
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState(movie = arguments))
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    // check user login status
    val isLoggedIn
        get() = UserManager.isLoggedIn

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

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]$this")
        Logger.i("------------------------------------")

        arguments.imdbID?.let { imdbID ->
            UserManager.userID?.let { observeScore(imdbID = imdbID, userID = it) }
            observeComments(imdbID = imdbID)
        }
    }

    fun navigateToPending() {
        _navigateToPending.value = arguments
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

    fun navigateToTrailer() {
        _navigateToTrailer.value = arguments
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

    private fun observeScore(imdbID: String, userID: String) {
        viewModelScope.launch {
            repository.getLiveScore(imdbID, userID).asFlow().collect { score ->
                Logger.i("DetailViewModel score = $score")
                _uiState.update { it.copy(userScore = score) }
            }
        }
    }

    private fun observeComments(imdbID: String) {
        viewModelScope.launch {
            repository.getLiveComments(imdbID).asFlow().collect { comments ->
                _uiState.update { it.copy(comments = comments) }
                loadMissingUsers(comments)
            }
        }
    }

    /** Loads the authors of [comments] that are not in the state yet. */
    private suspend fun loadMissingUsers(comments: List<Comment>) {
        val knownUserIds = _uiState.value.usersById.keys
        val missingUserIds = comments.map { it.userID }.distinct().filterNot { it in knownUserIds }
        if (missingUserIds.isEmpty()) return

        Logger.i("DetailViewModel loading users = $missingUserIds")
        when (val result = repository.getUsersByIdList(idList = missingUserIds)) {
            is Result.Success -> _uiState.update { state ->
                state.copy(usersById = state.usersById + result.data.associateBy(User::id))
            }
            else -> Logger.w("DetailViewModel getUsersByIdList = $result")
        }
    }
}
