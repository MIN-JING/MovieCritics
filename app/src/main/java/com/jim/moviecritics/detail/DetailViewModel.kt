package com.jim.moviecritics.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.google.firebase.firestore.FirebaseFirestore
import com.jim.moviecritics.R
import com.jim.moviecritics.data.Cast
import com.jim.moviecritics.data.Movie
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.Score
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

    private val _scores = MutableLiveData<List<Score>?>()

    val scores: LiveData<List<Score>?>
        get() = _scores

    private val _score = MutableLiveData<Score?>()

    val score: LiveData<Score?>
        get() = _score


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

        Logger.i("DetailViewModel init {}")
//        pushMockScore()
        movie.value?.imdbID?.let { getScoreResult(isInitial = true, imdbID = it, userID = 200001) }

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

    private fun getScoresResult(isInitial: Boolean = false, imdbID: String) {

        coroutineScope.launch {

            if (isInitial) _status.value = LoadApiStatus.LOADING

            val result = applicationRepository.getScores(imdbID)

            _scores.value = when (result) {
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

    private fun getScoreResult(isInitial: Boolean = false, imdbID: String, userID: Long) {

        coroutineScope.launch {

            if (isInitial) _status.value = LoadApiStatus.LOADING

            val result = applicationRepository.getScore(imdbID, userID)

            _score.value = when (result) {
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

    fun setRadarData(
        averageLeisure: Float,
        averageHit: Float,
        averageCast: Float,
        averageMusic: Float,
        averageStory: Float,
        userLeisure: Float,
        userHit: Float,
        userCast: Float,
        userMusic: Float,
        userStory: Float,
    ): RadarData {
        val averageRatingsList: ArrayList<RadarEntry>
                = arrayListOf(
            RadarEntry(averageLeisure),
            RadarEntry(averageHit),
            RadarEntry(averageCast),
            RadarEntry(averageMusic),
            RadarEntry(averageStory)
        )
        val userRatingsList: ArrayList<RadarEntry>
                = arrayListOf(
            RadarEntry(userLeisure),
            RadarEntry(userHit),
            RadarEntry(userCast),
            RadarEntry(userMusic),
            RadarEntry(userStory)
        )

        val averageRatingsSet = RadarDataSet(averageRatingsList, "Average Ratings")
        averageRatingsSet.color = R.color.teal_200
        averageRatingsSet.fillColor = R.color.teal_700
        averageRatingsSet.setDrawFilled(true)
        averageRatingsSet.fillAlpha = 160
        averageRatingsSet.lineWidth = 2F
        averageRatingsSet.isDrawHighlightCircleEnabled = true
        averageRatingsSet.setDrawHighlightIndicators(false)

        val userRatingsSet = RadarDataSet(userRatingsList, "Ratings By you")
        userRatingsSet.color = R.color.yellow
        userRatingsSet.fillColor = R.color.yellow
        userRatingsSet.setDrawFilled(true)
        userRatingsSet.fillAlpha = 160
        userRatingsSet.lineWidth = 2F
        userRatingsSet.isDrawHighlightCircleEnabled = true
        userRatingsSet.setDrawHighlightIndicators(false)

        val totalRatingsSet = ArrayList<IRadarDataSet>()
        totalRatingsSet.add(averageRatingsSet)
        totalRatingsSet.add(userRatingsSet)

        val radarData = RadarData(totalRatingsSet)
        radarData.setDrawValues(true)
        radarData.setValueTextSize(10F)
        radarData.setValueTextColor(R.color.purple_200)

        return radarData
    }

    fun showRadarChart(radarChart: RadarChart, radarData: RadarData) {
        radarChart.description.text = ""
        radarChart.description.setPosition(750F, 70F)
        radarChart.description.textSize = 50F
        radarChart.setDrawWeb(true)
//        radarChart.setBackgroundColor(Color.rgb(255, 102, 0))
//        radarChart.webLineWidth = 1f
//        radarChart.webColor = Color.rgb(0, 0, 0)
//        radarChart.webColorInner = Color.rgb(0, 0, 0)
//        radarChart.webLineWidthInner = 1f
        radarChart.isRotationEnabled = true

        val labels: Array<String> = arrayOf("Leisure", "Hit", "Cast", "Music", "Story")
        radarChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        radarChart.yAxis.axisMinimum = 0F
        radarChart.yAxis.axisMaximum = 5F
        radarChart.data = radarData
        radarChart.invalidate()
    }

    private fun pushMockScore() {
        val result = applicationRepository.loadMockScore()
        val scores = FirebaseFirestore.getInstance().collection("score")
        val document = scores.document()
        document.set(result)
    }
}