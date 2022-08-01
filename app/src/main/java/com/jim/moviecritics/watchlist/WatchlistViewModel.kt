package com.jim.moviecritics.watchlist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.Timestamp
import com.jim.moviecritics.MovieApplication
import com.jim.moviecritics.R
import com.jim.moviecritics.data.*
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.login.UserManager
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util.getString
import com.jim.moviecritics.work.WatchlistReminderWorker
import com.jim.moviecritics.work.WatchlistReminderWorker.Companion.nameKey
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.*

class WatchlistViewModel(
    private val applicationRepository: ApplicationRepository,
    private val arguments: User?
) : ViewModel() {

    private val _user = MutableLiveData<User>().apply {
        arguments?.let {
            value = it
        }
    }

    val user: LiveData<User>
        get() = _user

    var liveWatchListByUser = MutableLiveData<List<Watch>>()

    private val _timeStamp = MutableLiveData<Timestamp>()

    val timeStamp: LiveData<Timestamp>
        get() = _timeStamp

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

        UserManager.userId?.let { getLiveWatchListByUserResult(it) }
    }

    fun getFindsByImdbIDs(imdbIDs: List<String>) {
        val list = mutableListOf<Find>()
        coroutineScope.launch {
            _status.postValue(LoadApiStatus.LOADING)
            imdbIDs.forEachIndexed { index, imdbID ->
                Logger.i("Item WatchList request child $index")
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
            Logger.i("Item WatchList movieMap = $movieMap")
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
                    _error.postValue(getString(R.string.you_know_nothing))
                    if (isInitial) _status.postValue(LoadApiStatus.ERROR)
                    null
                }
            }
        }
    }

    private fun getLiveWatchListByUserResult(userID: String) {
        liveWatchListByUser = applicationRepository.getLiveWatchListByUser(userID)
        Logger.i("getLiveWatchListResult() liveComments.value = ${liveWatchListByUser.value}")
    }

    fun toDate(timestamp: Timestamp?): String {
        var date = ""
        if (timestamp != null) {
            date = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH).format(timestamp.toDate())
            Logger.i("date = $date")
        }
        return date
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

        val timePickerOnDataSetListener =
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                showHour = hour
                showMinute = minute
                Logger.i("Dialog selected year: $showYear, month: $showMonth," +
                        " day: $showDay, hour: $showHour, minute: $showMinute")
                calendar.set(Calendar.YEAR, showYear)
                calendar.set(Calendar.MONTH, showMonth)
                calendar.set(Calendar.DAY_OF_MONTH, showDay)
                calendar.set(Calendar.HOUR_OF_DAY, showHour)
                calendar.set(Calendar.MINUTE, showMinute)
                Logger.i("Dialog selected calendar.time = ${calendar.time}")
                _timeStamp.value = Timestamp(calendar.time)
            }

        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                showYear = year
                showMonth = month
                showDay = day

                TimePickerDialog(
                    context,
                    timePickerOnDataSetListener,
                    nowHour,
                    nowMinute,
                    true
                ).show()
            }

        DatePickerDialog(
            context,
            datePickerOnDataSetListener,
            nowYear,
            nowMonth,
            nowDay
        ).show()
    }

    fun pushSingleWatchListExpiration(watch: Watch) {
        coroutineScope.launch {
            when (val result = applicationRepository.pushSingleWatchListExpiration(watch)) {
                is Result.Success -> {
                    _error.value = null
                }
                is Result.Fail -> {
                    _error.value = result.error
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                }
                else -> {
                    _error.value = MovieApplication.instance.getString(R.string.you_know_nothing)
                }
            }
        }
    }

    internal fun scheduleReminder(
        duration: Long,
        unit: TimeUnit,
        movieTitle: String,
        context: Context
    ) {
        Logger.i("scheduleReminder()")
        val data = Data.Builder()
            .putString(nameKey, movieTitle)
            .build()

        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<WatchlistReminderWorker>()
            .setInitialDelay(duration, unit)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            movieTitle,
            ExistingWorkPolicy.REPLACE, oneTimeWorkRequest
        )
    }
}