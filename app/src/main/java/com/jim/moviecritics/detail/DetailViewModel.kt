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
import com.jim.moviecritics.R
import com.jim.moviecritics.data.*
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.login.UserManager
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.*


class DetailViewModel(
    private val applicationRepository: ApplicationRepository,
    private val arguments: Movie
    ) : ViewModel() {

//    private val movie = arguments

    private val user = UserManager.user

    private val _movie = MutableLiveData<Movie>().apply {
        value = arguments
    }

    val movie: LiveData<Movie>
        get() = _movie


//    private val _user = MutableLiveData<User>()
//
//    val user: LiveData<User>
//        get() = _user


    private val _scores = MutableLiveData<List<Score>?>()

    val scores: LiveData<List<Score>?>
        get() = _scores


    private val _score = MutableLiveData<Score?>()

    val score: LiveData<Score?>
        get() = _score


    var liveScore = MutableLiveData<Score>()


    private val _comments = MutableLiveData<List<Comment>>()

    val comments: LiveData<List<Comment>>
        get() = _comments


    var liveComments = MutableLiveData<List<Comment>>()


    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave


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

        movie.value?.imdbID?.let {
            getLiveCommentsResult(imdbID = it)
            user?.id?.let { userID -> getLiveScoreResult(imdbID = it, userID = userID) }
        }

    }

    fun navigateToPending(movie: Movie) {
        _navigateToPending.value = movie
    }

    fun onPendingNavigated() {
        _navigateToPending.value = null
    }

    fun leave() {
        _leave.value = true
    }

    private fun getLiveScoreResult(imdbID: String, userID: String) {
        Logger.i("getLiveScoreResult()")
        Logger.i("getLiveScoreResult() userID = $userID")
        Logger.i("getLiveScoreResult() imdbID = $imdbID")
        liveScore.value = applicationRepository.getLiveScore(imdbID, userID).value
        liveScore = applicationRepository.getLiveScore(imdbID, userID)
        Logger.i("getLiveScoreResult() liveScore = $liveScore")
        Logger.i("getLiveScoreResult() liveScore.value = ${liveScore.value}")
        _status.value = LoadApiStatus.DONE
    }


    private fun getLiveCommentsResult(imdbID: String) {
        liveComments = applicationRepository.getLiveComments(imdbID)
        Logger.i("getLiveCommentsResult() liveComments = $liveComments")
        Logger.i("getLiveCommentsResult() liveComments.value = ${liveComments.value}")
        _status.value = LoadApiStatus.DONE
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
}