package com.jim.moviecritics.profile.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.jim.moviecritics.R
import com.jim.moviecritics.data.Comment
import com.jim.moviecritics.data.Find
import com.jim.moviecritics.data.FindResult
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.login.UserManager
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.*

class ItemGuideViewModel(
    private val applicationRepository: ApplicationRepository
) : ViewModel() {

    private val user = UserManager.user

    var livePersonalComments = MutableLiveData<List<Comment>>()

    var movieMap = mapOf<String, Find>()

    private val _isMovieMapReady = MutableLiveData<Boolean>()

    val isMovieMapReady: LiveData<Boolean>
        get() = _isMovieMapReady

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

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]$this")
        Logger.i("------------------------------------")

        user?.id?.let { getLivePersonalCommentsResult(it) }
    }

    private fun getLivePersonalCommentsResult(userID: String) {
        livePersonalComments = applicationRepository.getLivePersonalComments(userID)
        _status.value = LoadApiStatus.DONE
    }

    fun timeStampToDate(timestamp: Timestamp): String {
        val date = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH).format(timestamp.toDate())
        Logger.i("date = $date")
        return date
    }

    fun getFindsByImdbIDs(imdbIDs: List<String>) {
        val list = mutableListOf<Find>()

        coroutineScope.launch {
            for (index in imdbIDs.indices) {
                Logger.i("Item Guide request child $index")
                Logger.i("imdbIDs[index] = ${imdbIDs[index]}")
                val result =
                    getFindResult(isInitial = true, imdbID = imdbIDs[index], index = index)
                Logger.i("getFindsByImdbIDs result = $result")

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
                        Logger.i("getFindsByImdbIDs list = $list")
                    }
                }
            }
            movieMap = imdbIDs.zip(list).toMap()
            Logger.i("Item Guide movieMap = $movieMap")
            _isMovieMapReady.value = true
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
