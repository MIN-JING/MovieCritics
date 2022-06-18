package com.jim.moviecritics.home


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.jim.moviecritics.R
import com.jim.moviecritics.data.*
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.jim.moviecritics.util.Util.getString



class HomeViewModel(private val applicationRepository: ApplicationRepository) : ViewModel() {

    val testComments = MutableLiveData<List<Comment>>()

    val testItems = MutableLiveData<PopularMoviesResult?>()

    private val _homeItems = MutableLiveData<List<HomeItem>>()

    val homeItems: LiveData<List<HomeItem>>
        get() = _homeItems

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // Handle navigation to detail
    private val _navigateToDetail = MutableLiveData<Movie>()

    val navigateToDetail: LiveData<Movie>
        get() = _navigateToDetail

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Call getMarketingHotsResult() on init so we can display status immediately.
     */
    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]$this")
        Logger.i("------------------------------------")

        getPopularMoviesResult(true)
        getCommentsResult(true)
//        loadMockCommentResult(true)
    }

    /**
     * track [StylishRepository.getMarketingHots]: -> [DefaultStylishRepository] : [StylishRepository] -> [StylishRemoteDataSource] : [StylishDataSource]
     */
    private fun getPopularMoviesResult(isInitial: Boolean = false) {

        coroutineScope.launch {

            if (isInitial) _status.value = LoadApiStatus.LOADING

            val result = applicationRepository.getPopularMovies()

            _homeItems.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    if (isInitial) _status.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    if (isInitial) _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    if (isInitial) _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = getString(R.string.you_know_nothing)
                    if (isInitial) _status.value = LoadApiStatus.ERROR
                    null
                }
            }
//            _refreshStatus.value = false
        }
    }

    private fun getCommentsResult(isInitial: Boolean = false) {

        coroutineScope.launch {

            if (isInitial) _status.value = LoadApiStatus.LOADING

            val result = applicationRepository.getComments()

            testComments.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    if (isInitial) _status.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    if (isInitial) _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    if (isInitial) _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = getString(R.string.you_know_nothing)
                    if (isInitial) _status.value = LoadApiStatus.ERROR
                    null
                }
            }
        }
    }

    private fun loadMockCommentResult(isInitial: Boolean = false) {
        coroutineScope.launch {

            if (isInitial) _status.value = LoadApiStatus.LOADING

            when (val result = applicationRepository.loadMockDataComment()) {
                is Result.Success -> {
                    _error.value = null
                    if (isInitial) _status.value = LoadApiStatus.DONE
//                    result.data

                    val comments = FirebaseFirestore.getInstance().collection("comment")
                    val document = comments.document()
                    document.set(result.data)
                }
                is Result.Fail -> {
                    _error.value = result.error
                    if (isInitial) _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    if (isInitial) _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = getString(R.string.you_know_nothing)
                    if (isInitial) _status.value = LoadApiStatus.ERROR
                    null
                }
            }
        }

    }
}

//_homeItems.value = when (result) {
//    is Result.Success -> {
//        _error.value = null
//        if (isInitial) _status.value = LoadApiStatus.DONE
//        result.data
//    }
//    is Result.Fail -> {
//        _error.value = result.error
//        if (isInitial) _status.value = LoadApiStatus.ERROR
//        null
//    }
//    is Result.Error -> {
//        _error.value = result.exception.toString()
//        if (isInitial) _status.value = LoadApiStatus.ERROR
//        null
//    }
//    else -> {
//        _error.value = getString(R.string.you_know_nothing)
//        if (isInitial) _status.value = LoadApiStatus.ERROR
//        null
//    }
//}