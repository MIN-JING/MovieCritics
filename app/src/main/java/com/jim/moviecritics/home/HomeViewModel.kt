package com.jim.moviecritics.home


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.jim.moviecritics.MovieApplication
import com.jim.moviecritics.R
import com.jim.moviecritics.data.*
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util.getString
import kotlinx.coroutines.*


class HomeViewModel(private val applicationRepository: ApplicationRepository) : ViewModel() {

    private val _comments = MutableLiveData<List<Comment>>()

    val comments: LiveData<List<Comment>>
        get() = _comments


    val pushTrend = MutableLiveData<PushTrend>()


    private val _homeItems = MutableLiveData<List<HomeItem>>()

    val homeItems: LiveData<List<HomeItem>>
        get() = _homeItems

    private val _detailItem = MutableLiveData<MovieDetailResult>()

    val detailItem: LiveData<MovieDetailResult>
        get() = _detailItem

    private val _creditItem = MutableLiveData<CreditResult>()

    val creditItem: LiveData<CreditResult>
        get() = _creditItem

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
        getCommentsResult(true)
//        loadMockCommentResult(true)

    }


    fun getMovieFull(id: Int) {
        Logger.i("fun navigateToDetail")
//        var totalCount = 2
            val movie = Movie()
            coroutineScope.launch {
                _detailItem.value = getMovieDetail(index = 0, id = id)
                _creditItem.value = getMovieCredit(index = 1, id = id)

                detailItem.value?.let {
                    Logger.i("detailItem.value = ${detailItem.value}")
                    movie.id = it.id
                    movie.imdbID = it.imdbID
                    movie.awards = null
                    movie.country = null
                    movie.genres = it.genres
                    movie.overview = it.overview
                    movie.posterUri = "https://image.tmdb.org/t/p/w185" + it.posterUri
                    movie.released = it.releaseDate
                    movie.runTime = it.runTime
                    movie.revenue = it.revenue
                    movie.salesTaiwan = null
                    movie.title = it.title
                    movie.trailerUri = null
                    movie.ratings = listOf()
                }

                creditItem.value?.let {
                    Logger.i("creditItem.value = ${creditItem.value}")

                    for (cast in it.casts) {
                        if (cast.profilePath != null) {
                            cast.profilePath = "https://image.tmdb.org/t/p/w185" + cast.profilePath
                            Logger.i("cast.profilePath = ${cast.profilePath}")
                        }
                    }

                    movie.casts = it.casts
                    movie.crews = it.crews

                    val writingList = mutableListOf<String>()
                    for (value in it.crews) {
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
                _navigateToDetail.value = movie
            }
    }

    fun navigateToDetail(movie: Movie) {
        _navigateToDetail.value = movie
    }

    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }

    /**
     * track [StylishRepository.getMarketingHots]: -> [DefaultStylishRepository] : [StylishRepository] -> [StylishRemoteDataSource] : [StylishDataSource]
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
//            _refreshStatus.value = false
        }
    }

    private suspend fun getMovieDetail(index:Int, id: Int): MovieDetailResult? {

        return withContext(Dispatchers.IO) {

            when (val result = applicationRepository.getMovieDetail(id)) {
                is Result.Success -> {
                    Logger.w("child $index result: ${result.data}")
                    result.data
                }
                is Result.Fail -> {
                    null
                }
                is Result.Error -> {
                    null
                }
                else -> {
                    null
                }
            }
        }
    }

    private suspend fun getMovieCredit(index: Int, id: Int): CreditResult? {

        return withContext(Dispatchers.IO) {

            when (val result = applicationRepository.getMovieCredit(id)) {
                is Result.Success -> {
                    Logger.w("child $index result: ${result.data}")
                    result.data
                }
                is Result.Fail -> {
                    null
                }
                is Result.Error -> {
                    null
                }
                else -> {
                    null
                }
            }
        }
    }


    private fun getCommentsResult(isInitial: Boolean = false) {

        coroutineScope.launch {

            if (isInitial) _status.value = LoadApiStatus.LOADING

            val result = applicationRepository.getComments()

            _comments.value = when (result) {
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

    private fun pushMockComment() {
        val result = applicationRepository.loadMockComment()
        val comment = FirebaseFirestore.getInstance().collection("comment")
        val document = comment.document()
        document.set(result)
    }

    fun pushPopularMovies(pushTrend: PushTrend) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = applicationRepository.pushPopularMovies(pushTrend)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                }
                else -> {
                    _error.value = MovieApplication.instance.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }
}

//fun getMovieFull(id: Int) {
//        getMovieDetail(isInitial = true, id = id)
//        getMovieCredit(isInitial = true, id = id)
//
//        val movie = Movie()
//
//        detailItem.value?.let {
//            Logger.i("detailItem.value = ${detailItem.value}")
//            movie.id = it.id
//            movie.imdbID = it.imdbID
//            movie.awards = null
//            movie.country = null
//            movie.genres = it.genres
//            movie.overview = it.overview
//            movie.posterUri = "https://image.tmdb.org/t/p/w185" + it.posterUri
//            movie.released = it.releaseDate
//            movie.runTime = it.runTime
//            movie.revenue = it.revenue
//            movie.salesTaiwan = null
//            movie.title = it.title
//            movie.trailerUri = null
//            movie.ratings = listOf()
//        }
//
//        creditItem.value?.let {
//            Logger.i("creditItem.value = ${creditItem.value}")
//            movie.casts = it.casts
//            movie.crews = it.crews
//
//            val writingList = mutableListOf<String>()
//            for (value in it.crews) {
//                if (value.job == "Director") {
//                    movie.director = value.name
//                    Logger.i("movie.director = ${movie.director}")
//                }
//                if (value.job == "Story") {
//                    writingList.add(value.name)
//                    Logger.i("writingList = $writingList")
//                }
//            }
//            movie.writing = writingList
//        }
//        Logger.i("movie = $movie")
//}



//    fun getMovieDetail(isInitial: Boolean = false, id: Int) {
//        coroutineScope.launch {
//
//            if (isInitial) _status.value = LoadApiStatus.LOADING
//
//            val result = applicationRepository.getMovieDetail(id)
//
//            _detailItem.value = when (result) {
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
//
//    fun getMovieCredit(isInitial: Boolean = false, id: Int) {
//        coroutineScope.launch {
//
//            if (isInitial) _status.value = LoadApiStatus.LOADING
//
//            val result = applicationRepository.getMovieCredit(id)
//
//            _creditItem.value = when (result) {
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

//    fun getMovieFull(id: Int) {
//        Logger.i("fun navigateToDetail")
//        getMovieDetail(isInitial = true, id = id)
//        getMovieCredit(isInitial = true, id = id)
//
//        val movie = Movie()
//
//        detailItem.value?.let {
//            Logger.i("detailItem.value = ${detailItem.value}")
//            movie.id = it.id
//            movie.imdbID = it.imdbID
//            movie.awards = null
//            movie.country = null
//            movie.genres = it.genres
//            movie.overview = it.overview
//            movie.posterUri = "https://image.tmdb.org/t/p/w185" + it.posterUri
//            movie.released = it.releaseDate
//            movie.runTime = it.runTime
//            movie.revenue = it.revenue
//            movie.salesTaiwan = null
//            movie.title = it.title
//            movie.trailerUri = null
//            movie.ratings = listOf()
//        }
//
//        creditItem.value?.let {
//            Logger.i("creditItem.value = ${creditItem.value}")
//            movie.casts = it.casts
//            movie.crews = it.crews
//
//            val writingList = mutableListOf<String>()
//            for (value in it.crews) {
//                if (value.job == "Director") {
//                    movie.director = value.name
//                    Logger.i("movie.director = ${movie.director}")
//                }
//                if (value.job == "Story") {
//                    writingList.add(value.name)
//                    Logger.i("writingList = $writingList")
//                }
//            }
//            movie.writing = writingList
//        }
//        Logger.i("movie = $movie")
//    }


//        _navigateToDetail.value = detailItem.value?.let {
//            Movie(
//                id = it.id,
//                casts = listOf(),
//                imdbID = it.imdbID,
//                awards = null,
//                country = null,
//                director = null,
//                genres = it.genres,
//                overview = it.overview,
//                posterUri = "https://image.tmdb.org/t/p/w185" + it.posterUri,
//                released = it.releaseDate,
//                runTime = it.runTime,
//                revenue = null,
//                salesTaiwan = null,
//                title = it.title,
//                trailerUri = null,
//                writing = listOf(),
//                ratings = listOf(),
//                crews = listOf())
//        }