package com.jim.moviecritics.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.jim.moviecritics.data.Comment
import com.jim.moviecritics.data.Movie
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.login.UserManager
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val repository: Repository,
    private val arguments: Movie
) : ViewModel() {

    private val _movie = MutableLiveData<Movie>().apply {
        value = arguments
    }

    val movie: LiveData<Movie>
        get() = _movie


    private val comment = Comment()

    val content = MutableLiveData<String>()

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    private val _leave = MutableLiveData<Boolean?>()

    val leave: LiveData<Boolean?>
        get() = _leave

    private val _invalidComment = MutableLiveData<Int>()

    val invalidComment: LiveData<Int>
        get() = _invalidComment

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

        comment.imdbID = movie.value?.imdbID.toString()
        comment.userID = UserManager.userID.toString()
    }

    fun leave() {
        _leave.value = true
    }

    fun onLeaveCompleted() {
        _leave.value = null
    }

    fun toGenres(): String {
        var genres = ""

        movie.value?.genres?.let {
            if (it.isNotEmpty()) {
                for (genre in it) {
                    genres = genres + genre.name + ", "
                }
                Logger.i("genres = $genres")
            }
        }
        return genres
    }

    fun dateToday(): String {
        val today = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.ENGLISH)
            .format(Timestamp.now().toDate())
        Logger.i("today = $today")
        return today
    }

    private fun pushComment(comment: Comment) {
        coroutineScope.launch {
            comment.createdTime = Timestamp.now()
            _status.value = LoadApiStatus.LOADING
            when (val result = repository.pushComment(comment)) {
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

    fun prepareComment() {
        when {
            content.value == null -> _invalidComment.value = INVALID_FORMAT_COMMENT_EMPTY

            content.value != null -> {
                comment.content = content.value.toString()
                Logger.i("pushComment(comment) = $comment")
                pushComment(comment)
                leave()
            }

            else -> _invalidComment.value = NO_ONE_KNOWS
        }
    }

    companion object {
        const val INVALID_FORMAT_COMMENT_EMPTY = 0x11
        const val NO_ONE_KNOWS = 0x21
    }
}
