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

    var livePersonalComments = MutableLiveData<List<Comment>>()

    var movieMap = mapOf<String, Find>()

    private val _isMovieMapReady = MutableLiveData<Boolean>()

    val isMovieMapReady: LiveData<Boolean>
        get() = _isMovieMapReady


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

        UserManager.userId?.let { getLivePersonalCommentsResult(it) }
    }

    private fun getLivePersonalCommentsResult(userID: String) {
        livePersonalComments = applicationRepository.getLivePersonalComments(userID)
    }

    fun timeStampToDate(timestamp: Timestamp): String {
        val date = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
                    .format(timestamp.toDate())
        Logger.i("date = $date")
        return date
    }

    fun getFindsByImdbIDs(imdbIDs: List<String>) {
        val list = mutableListOf<Find>()
        coroutineScope.launch {
            _status.postValue(LoadApiStatus.LOADING)
            imdbIDs.forEachIndexed { index, imdbID ->
                Logger.i("Item Guide request child $index")
                Logger.i("imdbID = $imdbID")
                val result =
                    getFindResult(isInitial = true, imdbID = imdbID, index = index)
                Logger.i("getFindsByImdbIDs result = $result")
                if (!result?.finds.isNullOrEmpty()) {
                    result?.finds?.forEach { find ->
                        Logger.i("result find = $find")
                        if (!find.posterUri.isNullOrEmpty()) {
                            find.posterUri = "https://image.tmdb.org/t/p/w185${find.posterUri}"
                        }
                        if (!find.backdrop.isNullOrEmpty()) {
                            find.backdrop = "https://image.tmdb.org/t/p/w185${find.backdrop}"
                        }
                        list.add(find)
                        Logger.i("getFindsByImdbIDs find list = $list")
                    }
                }
            }
            movieMap = imdbIDs.zip(list).toMap()
            Logger.i("Item Guide movieMap = $movieMap")
            _isMovieMapReady.value = true
            _status.postValue(LoadApiStatus.DONE)
        }
    }

    private suspend fun getFindResult(
        isInitial: Boolean = false,
        imdbID: String,
        index: Int
    ): FindResult? {
        return withContext(Dispatchers.IO) {
            when (val result = applicationRepository.getFind(imdbID)) {
                is Result.Success -> {
                    _error.postValue(null)
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