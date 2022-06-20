package com.jim.moviecritics.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.jim.moviecritics.R
import com.jim.moviecritics.data.Movie
import com.jim.moviecritics.data.MovieDetailResult
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DetailViewModel(
    private val applicationRepository: ApplicationRepository,
    private val arguments: Movie
    ) : ViewModel() {

    private val _movie = MutableLiveData<Movie>().apply {
        value = arguments
    }

    val movie: LiveData<Movie>
        get() = _movie

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    private val _leaveDetail = MutableLiveData<Boolean>()

    val leaveDetail: LiveData<Boolean>
        get() = _leaveDetail


    private val _navigateToPending = MutableLiveData<Movie?>()

    val navigateToPending: LiveData<Movie?>
        get() = _navigateToPending


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

        pushMockScore()
    }

    fun navigateToPending(movie: Movie) {
        _navigateToPending.value = movie
    }

    fun onPendingNavigated() {
        _navigateToPending.value = null
    }

    fun leaveDetail() {
        _leaveDetail.value = true
    }

    private fun pushMockScore() {
        val result = applicationRepository.loadMockScore()
        val scores = FirebaseFirestore.getInstance().collection("score")
        val document = scores.document()
        document.set(result)
    }
}