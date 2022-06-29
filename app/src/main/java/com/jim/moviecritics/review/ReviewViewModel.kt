package com.jim.moviecritics.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.jim.moviecritics.MovieApplication
import com.jim.moviecritics.R
import com.jim.moviecritics.data.Comment
import com.jim.moviecritics.data.Movie
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReviewViewModel(
    private val applicationRepository: ApplicationRepository,
    private val arguments: Movie
) : ViewModel() {

    private val _movie = MutableLiveData<Movie>().apply {
        value = arguments
    }

    val movie: LiveData<Movie>
        get() = _movie


    private val _comment = MutableLiveData<Comment>().apply {
        value = arguments.imdbID?.let {
            Comment(
                userID = 200001L,
                imdbID = it
            )
        }
    }

    val comment: LiveData<Comment>
        get() = _comment


//    private val _liveComment = MutableLiveData<Comment>()
//
//    val liveComment: LiveData<Comment>
//        get() = _liveComment
//
//    val test = MutableLiveData<String>()
//
//    val test2 = MutableLiveData<Comment>()



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
        val today = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.ENGLISH).format(Timestamp.now().toDate())
        Logger.i("today = $today")
        return today
    }

    private fun pushComment(comment: Comment) {

        coroutineScope.launch {

            _comment.value?.createdTime = Timestamp.now()

            _status.value = LoadApiStatus.LOADING

            when (val result = applicationRepository.pushComment(comment)) {
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

    fun prepareComment() {
        when {
            comment.value?.content?.isEmpty() == true -> _invalidComment.value = INVALID_FORMAT_COMMENT_EMPTY
            comment.value?.content?.isEmpty() == false -> comment.value?.let{
                pushComment(it)
            }
            else -> _invalidComment.value = NO_ONE_KNOWS
        }
    }

    companion object {
        const val INVALID_FORMAT_COMMENT_EMPTY = 0x11
        const val NO_ONE_KNOWS = 0x21
    }
}