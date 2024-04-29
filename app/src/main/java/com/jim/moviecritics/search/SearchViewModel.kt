package com.jim.moviecritics.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jim.moviecritics.R
import com.jim.moviecritics.data.LookItem
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: Repository) : ViewModel() {

    companion object {
        const val INVALID_FORMAT_SEARCH_KEY_EMPTY = 0x11
        const val NO_ONE_KNOWS = 0x21
    }

    var searchQuery by mutableStateOf("")
        private set

    val searchKey = MutableLiveData<String>()

    private val _lookItems = MutableStateFlow<List<LookItem>>(emptyList())
    val lookItems: StateFlow<List<LookItem>> = _lookItems.asStateFlow()

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


    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]$this")
        Logger.i("------------------------------------")
    }


    fun onSearchQueryChanged(query: String) {
        searchQuery = query
        getSearchResult(queryKey = query)
    }

    private fun getSearchResult(isInitial: Boolean = false, queryKey: String) {
        viewModelScope.launch {
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
                    emptyList()
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    if (isInitial) _status.value = LoadApiStatus.ERROR
                    emptyList()
                }
                else -> {
                    _error.value = Util.getString(R.string.you_know_nothing)
                    if (isInitial) _status.value = LoadApiStatus.ERROR
                    emptyList()
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
}
