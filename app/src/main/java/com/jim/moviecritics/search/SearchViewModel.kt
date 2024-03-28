package com.jim.moviecritics.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jim.moviecritics.R
import com.jim.moviecritics.data.LookItem
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: Repository) : ViewModel() {

    var searchQuery by mutableStateOf("")
        private set

//    val searchResults: StateFlow<List<LookItem>> =
//        snapshotFlow { searchQuery }
//            .combine(moviesFlow) { searchQuery, movies ->
//                when {
//                    searchQuery.isNotEmpty() -> movies.filter { movie ->
//                        movie.name.contains(searchQuery, ignoreCase = true)
//                    }
//                    else -> movies
//                }
//            }

    private val _lookItems = MutableLiveData<List<LookItem>>()

    val lookItems: LiveData<List<LookItem>>
        get() = _lookItems

    val searchKey = MutableLiveData<String>()

    private val _invalidSearch = MutableLiveData<Int>()

    val invalidSearch: LiveData<Int>
        get() = _invalidSearch

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

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

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]$this")
        Logger.i("------------------------------------")
    }

    private fun getSearchResult(isInitial: Boolean = false, queryKey: String) {
        coroutineScope.launch {
            if (isInitial) _status.value = LoadApiStatus.LOADING

            val result = repository.getSearchMulti(queryKey)

            _lookItems.value = when (result) {
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
                    _error.value = Util.getString(R.string.you_know_nothing)
                    if (isInitial) _status.value = LoadApiStatus.ERROR
                    null
                }
            }
        }
    }

    fun prepareSearch() {
        when {
            searchKey.value.isNullOrEmpty()
            -> _invalidSearch.value = INVALID_FORMAT_SEARCH_KEY_EMPTY

            !searchKey.value.isNullOrEmpty() -> searchKey.value?.let {
                getSearchResult(isInitial = true, it)
            }
            else -> _invalidSearch.value = NO_ONE_KNOWS
        }
    }

    companion object {
        const val INVALID_FORMAT_SEARCH_KEY_EMPTY = 0x11
        const val NO_ONE_KNOWS = 0x21
    }
}
