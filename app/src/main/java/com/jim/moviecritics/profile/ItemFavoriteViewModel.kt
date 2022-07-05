package com.jim.moviecritics.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jim.moviecritics.R
import com.jim.moviecritics.data.Find
import com.jim.moviecritics.data.FindResult
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.login.UserManager
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util
import kotlinx.coroutines.*

class ItemFavoriteViewModel(
    private val applicationRepository: ApplicationRepository
) : ViewModel() {

    var livePersonalFavorites = MutableLiveData<List<String>>()

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
     * service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Get [User] profile data when user is null
     */
    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]$this")
        Logger.i("------------------------------------")

        UserManager.userId?.let {
            getLivePersonalFavoritesResult(it)
        }

    }

    private fun getLivePersonalFavoritesResult(userID: String) {
        livePersonalFavorites = applicationRepository.getLivePersonalFavorites(userID)
        _status.value = LoadApiStatus.DONE
    }


    fun getFavoritesFull(favorites: List<String>) {

        var totalCount = favorites.size
        Logger.i("totalCount = $totalCount")

        val list = mutableListOf<Find>()

        coroutineScope.launch {

            for (index in 0 until totalCount) {
                Logger.i("Item Favorite request child $index")
                Logger.i("favorites[index] = ${favorites[index]}")
                val result =
                    getFindResult(isInitial = true, index = index, imdbID = favorites[index])
                Logger.i("getFavoritesFull result = $result")

                if (result?.finds != null) {
                    for (value in result.finds) {
                        Logger.i("result?.finds value = $value")
                        list.add(value)
                        Logger.i("getFavoritesFull list = $list")
                    }
                }
                delay(200)
//                totalCount--

                if (totalCount == 0) {
                    Logger.d("Item Favorite request the last one")
                    Logger.i("Item Favorite request result all = $list")
                }
            }

        }
    }

    private suspend fun getFindResult(isInitial: Boolean = false, imdbID: String, index: Int): FindResult? {

        return withContext(Dispatchers.IO) {

            if (isInitial) _status.postValue(LoadApiStatus.LOADING)

            when (val result = applicationRepository.getFind(imdbID)) {
                is Result.Success -> {
                    _error.postValue(null)
                    if (isInitial) _status.postValue(LoadApiStatus.DONE)
                    Logger.w("child $index result: ${result.data}")
                    result.data
                }
                is Result.Fail -> {
                    _error.postValue(result.error)
                    if (isInitial) _status.postValue(LoadApiStatus.ERROR)
                    null
                }
                is Result.Error -> {
                    _error.postValue(result.exception.toString())
                    if (isInitial) _status.postValue(LoadApiStatus.ERROR)
                    null
                }
                else -> {
                    _error.postValue(Util.getString(R.string.you_know_nothing))
                    if (isInitial) _status.postValue(LoadApiStatus.ERROR)
                    null
                }
            }
        }
    }
}