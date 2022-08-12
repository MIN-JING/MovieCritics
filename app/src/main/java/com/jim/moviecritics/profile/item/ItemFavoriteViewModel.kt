package com.jim.moviecritics.profile.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jim.moviecritics.R
import com.jim.moviecritics.data.Find
import com.jim.moviecritics.data.FindResult
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.login.UserManager
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util
import kotlinx.coroutines.*

class ItemFavoriteViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _finds = MutableLiveData<List<Find>>()

    val finds: LiveData<List<Find>>
        get() = _finds

    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

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

        UserManager.user?.liked?.let { getFavoritesFull(it) }
    }

    private fun getFavoritesFull(favorites: List<String>) {
        val list = mutableListOf<Find>()
        _status.value = LoadApiStatus.LOADING

        coroutineScope.launch {
            for (index in favorites.indices) {
                Logger.i("Item Favorite request child $index")
                Logger.i("favorites[index] = ${favorites[index]}")
                val result =
                    getFindResult(imdbID = favorites[index], index = index)
                Logger.i("getFavoritesFull result = $result")

                if (result?.finds != null) {
                    for (value in result.finds) {
                        Logger.i("result?.finds value = $value")
                        if (value.posterUri != null) {
                            value.posterUri = "https://image.tmdb.org/t/p/w185" + value.posterUri
                        }
                        if (value.backdrop != null) {
                            value.backdrop = "https://image.tmdb.org/t/p/w185" + value.backdrop
                        }
                        list.add(value)
                        Logger.i("getFavoritesFull list = $list")
                    }
                }
            }
            _finds.value = list
            Logger.i("Item Favorite getFavoritesFull list = $list")
            _status.postValue(LoadApiStatus.DONE)
        }
    }

    private suspend fun getFindResult(imdbID: String, index: Int): FindResult? {
        return withContext(Dispatchers.IO) {
            when (val result = repository.getFind(imdbID)) {
                is Result.Success -> {
                    _error.postValue(null)
                    Logger.w("child $index result: ${result.data}")
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
                    _error.postValue(Util.getString(R.string.you_know_nothing))
                    _status.postValue(LoadApiStatus.ERROR)
                    null
                }
            }
        }
    }
}
