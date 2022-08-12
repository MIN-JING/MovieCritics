package com.jim.moviecritics.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jim.moviecritics.R
import com.jim.moviecritics.data.*
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util.getString
import kotlin.math.roundToInt
import kotlinx.coroutines.*

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private val _homeItems = MutableLiveData<List<HomeItem>>()

    val homeItems: LiveData<List<HomeItem>>
        get() = _homeItems

    private val movie = Movie()

    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    private val _navigateToDetail = MutableLiveData<Movie?>()

    val navigateToDetail: LiveData<Movie?>
        get() = _navigateToDetail

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

        getPopularMoviesResult()
    }

    fun getMovieFull(id: Int) {
        coroutineScope.launch {
            _status.postValue(LoadApiStatus.LOADING)
            val detailResult = getMovieDetail(isInitial = true, index = 0, id = id)
            val creditResult = getMovieCredit(isInitial = true, index = 1, id = id)
            detailResultToMovie(detailResult)
            creditResultToMovie(creditResult)
            _status.postValue(LoadApiStatus.DONE)
            navigateToDetail(movie)
        }
    }

    private fun navigateToDetail(movie: Movie) {
        _navigateToDetail.value = movie
    }

    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }

    private fun getPopularMoviesResult() {
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING
            val result = repository.getPopularMovies()
            _homeItems.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
        }
    }

    private suspend fun getMovieDetail(
        isInitial: Boolean = false,
        index: Int,
        id: Int
    ): MovieDetailResult? {
        return withContext(Dispatchers.IO) {
            when (val result = repository.getMovieDetail(id)) {
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

    private suspend fun getMovieCredit(
        isInitial: Boolean = false,
        index: Int,
        id: Int
    ): CreditResult? {
        return withContext(Dispatchers.IO) {
            when (val result = repository.getMovieCredit(id)) {
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

    private fun detailResultToMovie(detailResult: MovieDetailResult?) {
        detailResult?.let { movieDetailResult ->
            Logger.i("movieDetailResult = $detailResult")
            movie.id = movieDetailResult.id
            movie.imdbID = movieDetailResult.imdbID
            movie.awards = null
            movie.country = null
            movie.genres = movieDetailResult.genres
            movie.overview = movieDetailResult.overview
            if (!movieDetailResult.posterUri.isNullOrEmpty()) {
                movie.posterUri = "https://image.tmdb.org/t/p/w185" + movieDetailResult.posterUri
            }
            movie.released = movieDetailResult.releaseDate
            movie.runtime = movieDetailResult.runtime
            movie.revenue = movieDetailResult.revenue
            movie.salesTaiwan = null
            movie.title = movieDetailResult.title
            movie.trailerUri = null
            movie.ratings = listOf()
            movie.voteAverage = ((movieDetailResult.average * 10).roundToInt() / 50).toFloat()
            if (!movieDetailResult.videos.results.isNullOrEmpty()) {
                val youtubeKey = movieDetailResult.videos.results.maxByOrNull { it.published }?.key
                youtubeKey?.let {
                    Logger.i("youtubeKey = $youtubeKey")
                    movie.trailerUri = "https://www.youtube.com/watch?v=$youtubeKey"
                    Logger.i("HomeViewModel movie.trailerUri = ${movie.trailerUri}")
                }
            }
        }
    }

    private fun creditResultToMovie(creditResult: CreditResult?) {
        creditResult?.let { movieCreditResult ->
            Logger.i("movieCreditResult = $creditResult")
            movieCreditResult.casts.forEach { cast ->
                if (!cast.profilePath.isNullOrEmpty()) {
                    cast.profilePath = "https://image.tmdb.org/t/p/w185" + cast.profilePath
                }
            }
            movie.casts = movieCreditResult.casts
            movie.crews = movieCreditResult.crews
            movieCreditResult.crews.forEach { crew ->
                if (crew.job == "Director") {
                    movie.director = crew.name
                    Logger.i("movie.director = ${movie.director}")
                }
                if (crew.job == "Story") {
                    movie.writing.add(crew.name)
                    Logger.i("movie.writing = ${movie.writing}")
                }
            }
        }
    }
}
