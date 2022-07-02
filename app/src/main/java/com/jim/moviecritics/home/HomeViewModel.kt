package com.jim.moviecritics.home


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jim.moviecritics.R
import com.jim.moviecritics.data.*
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util.getString
import kotlinx.coroutines.*
import kotlin.math.roundToInt


class HomeViewModel(private val applicationRepository: ApplicationRepository) : ViewModel() {

    private val _homeItems = MutableLiveData<List<HomeItem>>()

    val homeItems: LiveData<List<HomeItem>>
        get() = _homeItems

    private val _comments = MutableLiveData<List<Comment>>()

    val comments: LiveData<List<Comment>>
        get() = _comments

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // Handle navigation to detail
    private val _navigateToDetail = MutableLiveData<Movie?>()

    val navigateToDetail: LiveData<Movie?>
        get() = _navigateToDetail

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Call getMarketingHotsResult() on init so we can display status immediately.
     */
    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]$this")
        Logger.i("------------------------------------")

        getPopularMoviesResult(true)
//        getCommentsResult(false)
    }


    fun getMovieFull(id: Int) {

            val movie = Movie()
            coroutineScope.launch {
                val detailResult = getMovieDetail(isInitial = true, index = 0, id = id)
                val creditResult = getMovieCredit(isInitial = true, index = 1, id = id)

                detailResult?.let { movieDetailResult ->
                    Logger.i("movieDetailResult = $detailResult")
                    movie.id = movieDetailResult.id
                    movie.imdbID = movieDetailResult.imdbID
                    movie.awards = null
                    movie.country = null
                    movie.genres = movieDetailResult.genres
                    movie.overview = movieDetailResult.overview
                    if (movieDetailResult.posterUri != null) {
                        movie.posterUri = "https://image.tmdb.org/t/p/w185" + movieDetailResult.posterUri
                    }
                    movie.released = movieDetailResult.releaseDate
                    movie.runtime = movieDetailResult.runtime
                    movie.revenue = movieDetailResult.revenue
                    movie.salesTaiwan = null
                    movie.title = movieDetailResult.title
                    movie.trailerUri = null
                    movie.ratings = listOf()
                    Logger.i("it.average = ${movieDetailResult.average}")
                    movie.voteAverage = ((movieDetailResult.average * 10).roundToInt() / 50).toFloat()
                    Logger.i("movie.voteAverage = ${movie.voteAverage}")
                }

                creditResult?.let { movieCreditResult ->
                    Logger.i("movieCreditResult = $creditResult")

                    for (cast in movieCreditResult.casts) {
                        if (cast.profilePath != null) {
                            cast.profilePath = "https://image.tmdb.org/t/p/w185" + cast.profilePath
                        }
                    }

                    movie.casts = movieCreditResult.casts
                    movie.crews = movieCreditResult.crews

                    val writingList = mutableListOf<String>()

                    for (value in movieCreditResult.crews) {
                        if (value.job == "Director") {
                            movie.director = value.name
                            Logger.i("movie.director = ${movie.director}")
                        }
                        if (value.job == "Story") {
                            writingList.add(value.name)
                            Logger.i("writingList = $writingList")
                        }
                    }
                    movie.writing = writingList
                }
                Logger.i("movie = $movie")
                navigateToDetail(movie)
//                _navigateToDetail.value = movie
            }
    }

    fun navigateToDetail(movie: Movie) {
        _navigateToDetail.value = movie
    }

    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }

    /**
     * track [ApplicationRepository.getPopularMovies]: -> [DefaultApplicationRepository] : [ApplicationRepository] -> [ApiDataSource] : [ApplicationDataSource]
     */
    private fun getPopularMoviesResult(isInitial: Boolean = false) {

        coroutineScope.launch {

            if (isInitial) _status.value = LoadApiStatus.LOADING

            val result = applicationRepository.getPopularMovies()

            _homeItems.value = when (result) {
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
                    _error.value = getString(R.string.you_know_nothing)
                    if (isInitial) _status.value = LoadApiStatus.ERROR
                    null
                }
            }
        }
    }

    private suspend fun getMovieDetail(isInitial: Boolean = false, index: Int, id: Int): MovieDetailResult? {

        return withContext(Dispatchers.IO) {

            if (isInitial) _status.postValue(LoadApiStatus.LOADING)

            when (val result = applicationRepository.getMovieDetail(id)) {
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
                    _error.postValue(getString(R.string.you_know_nothing))
                    if (isInitial) _status.postValue(LoadApiStatus.ERROR)
                    null
                }
            }
        }
    }

    private suspend fun getMovieCredit(isInitial: Boolean = false, index: Int, id: Int): CreditResult? {

        return withContext(Dispatchers.IO) {

            withContext(Dispatchers.Main) {
                if (isInitial) _status.value = LoadApiStatus.LOADING
            }

            when (val result = applicationRepository.getMovieCredit(id)) {
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
                    _error.postValue(getString(R.string.you_know_nothing))
                    if (isInitial) _status.postValue(LoadApiStatus.ERROR)
                    null
                }
            }
        }
    }


//    private fun getCommentsResult(isInitial: Boolean = false) {
//
//        coroutineScope.launch {
//
//            if (isInitial) _status.value = LoadApiStatus.LOADING
//
//            val result = applicationRepository.getComments()
//
//            _comments.value = when (result) {
//                is Result.Success -> {
//                    _error.value = null
//                    if (isInitial) _status.value = LoadApiStatus.DONE
//                    result.data
//                }
//                is Result.Fail -> {
//                    _error.value = result.error
//                    if (isInitial) _status.value = LoadApiStatus.ERROR
//                    null
//                }
//                is Result.Error -> {
//                    _error.value = result.exception.toString()
//                    if (isInitial) _status.value = LoadApiStatus.ERROR
//                    null
//                }
//                else -> {
//                    _error.value = getString(R.string.you_know_nothing)
//                    if (isInitial) _status.value = LoadApiStatus.ERROR
//                    null
//                }
//            }
//        }
//    }
}