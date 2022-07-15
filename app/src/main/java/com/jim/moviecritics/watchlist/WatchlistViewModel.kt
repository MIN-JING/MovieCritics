package com.jim.moviecritics.watchlist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.jim.moviecritics.R
import com.jim.moviecritics.data.*
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.login.UserManager
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util
import kotlinx.coroutines.*
import java.util.*


class WatchlistViewModel(
    private val applicationRepository: ApplicationRepository,
    private val arguments: User?
    ) : ViewModel() {

    // After login to Firebase server through Google, at the same time we can get user info to provide to display ui
    private val _user = MutableLiveData<User>().apply {
        arguments?.let {
            value = it
        }
    }

    val user: LiveData<User>
        get() = _user


    private val _finds = MutableLiveData<List<Find>>()

    val finds: LiveData<List<Find>>
        get() = _finds

    var liveWatchListByUser = MutableLiveData<List<Watch>>()

    private val _timeStamp = MutableLiveData<Timestamp>()

    val timeStamp: LiveData<Timestamp>
        get() = _timeStamp


    var findsMap = mapOf<Int, Find>()

    var movieMap = mapOf<String, Find>()


    private val _isMovieMapReady = MutableLiveData<Boolean>()

    val isMovieMapReady: LiveData<Boolean>
        get() = _isMovieMapReady

    val isCalendar = MutableLiveData<Boolean>()


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

        if (user.value == null) {
            Logger.i("Watchlist ViewModel init if user.value == null")
            _user.value = UserManager.user
        }

//        user.value?.watchlist?.let { getWatchListFull(it) }
        user.value?.id?.let { getLiveWatchListByUserResult(it) }

    }

    fun getWatchListFull(watchList: List<String>) {
        val list = mutableListOf<Find>()

        coroutineScope.launch {
            for (index in watchList.indices) {
                Logger.i("Item WatchList request child $index")
                Logger.i("watchList[index] = ${watchList[index]}")
                val result =
                    getFindResult(isInitial = true, imdbID = watchList[index], index = index)
                Logger.i("getWatchListFull result = $result")

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
                        Logger.i("getWatchListFull list = $list")
                    }
                }
            }
            _finds.value = list

//            findsMap = list.mapIndexed { index, find ->
//                index to find
//            }.toMap()
//            Logger.i("findsMap = $findsMap")

            movieMap = watchList.zip(list).toMap()
            Logger.i("movieMap = $movieMap")

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

    private fun getLiveWatchListByUserResult(userID: String) {
        liveWatchListByUser = applicationRepository.getLiveWatchListByUser(userID)
        Logger.i("getLiveWatchListResult() liveComments = $liveWatchListByUser")
        Logger.i("getLiveWatchListResult() liveComments.value = ${liveWatchListByUser.value}")
        _status.value = LoadApiStatus.DONE
    }

    fun showDateTimeDialog(context: Context) {
        val calendar = Calendar.getInstance()
        val nowYear = calendar.get(Calendar.YEAR)
        val nowMonth = calendar.get(Calendar.MONTH)
        val nowDay = calendar.get(Calendar.DAY_OF_MONTH)
        val nowHour = calendar.get(Calendar.HOUR_OF_DAY)
        val nowMinute = calendar.get(Calendar.MINUTE)

        var showYear = 0
        var showMonth = 0
        var showDay = 0
        var showHour: Int
        var showMinute: Int

//        var timestamp: Timestamp


        val timePickerOnDataSetListener =
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                showHour = hour
                showMinute = minute
                Logger.i("hour: $showHour, minute: $showMinute")
                Logger.i("Dialog selected time year: $showYear, month: $showMonth, day: $showDay, hour: $showHour, minute: $showMinute")
                calendar.set(Calendar.YEAR, showYear)
                calendar.set(Calendar.MONTH, showMonth)
                calendar.set(Calendar.DAY_OF_MONTH, showDay)
                calendar.set(Calendar.HOUR_OF_DAY, showHour)
                calendar.set(Calendar.MINUTE, showMinute)
                Logger.i("Dialog selected calendar.time = ${calendar.time}")
                _timeStamp.value = Timestamp(calendar.time)
//                Logger.i("timeStamp = $timestamp")
            }

        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
//                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE)
//                setText(sdf.format(calendar.time))
                showYear = year
                showMonth = month
                showDay = day
                Logger.i("year: $showYear, month: $showMonth, day: $showDay")

                TimePickerDialog(
                    context,
                    timePickerOnDataSetListener,
                    nowHour,
                    nowMinute,
                    true).show()
            }

        DatePickerDialog(
            context,
            datePickerOnDataSetListener,
            nowYear,
            nowMonth,
            nowDay).show()


        Logger.i("nowYear = $nowYear, nowMonth = $nowMonth, nowDay = $nowDay, nowHour = $nowHour, nowMinute = $nowMinute")
//        Logger.i("Dialog selected time year: $showYear, month: $showMonth, day: $showDay, hour: $showHour, minute: $showMinute")
    }
}