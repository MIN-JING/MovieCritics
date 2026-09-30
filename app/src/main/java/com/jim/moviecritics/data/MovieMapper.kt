package com.jim.moviecritics.data

import com.jim.moviecritics.util.Logger

private const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w185"

/**
 * Combine the TMDB movie detail and credit responses into the [Movie] used by the detail page.
 */
fun buildMovie(detailResult: MovieDetailResult?, creditResult: CreditResult?): Movie {
    val movie = Movie()

    detailResult?.let { movieDetailResult ->
        movie.id = movieDetailResult.id
        movie.imdbID = movieDetailResult.imdbID
        movie.awards = null
        movie.country = null
        movie.genres = movieDetailResult.genres
        movie.overview = movieDetailResult.overview
        if (!movieDetailResult.posterUri.isNullOrEmpty()) {
            movie.posterUri = TMDB_IMAGE_BASE_URL + movieDetailResult.posterUri
        }
        movie.released = movieDetailResult.releaseDate
        movie.runtime = movieDetailResult.runtime
        movie.revenue = movieDetailResult.revenue
        movie.salesTaiwan = null
        movie.title = movieDetailResult.title
        movie.trailerUri = null
        movie.ratings = listOf()
        movie.voteAverage = movieDetailResult.average / 2
        Logger.i("movieDetailResult.average = ${movieDetailResult.average}")
        Logger.i("movie.voteAverage = ${movie.voteAverage}")
        if (!movieDetailResult.videos.results.isNullOrEmpty()) {
            val youtubeKey = movieDetailResult.videos.results.maxByOrNull { it.published }?.key
            youtubeKey?.let {
                movie.trailerUri = "https://www.youtube.com/watch?v=$youtubeKey"
                Logger.i("movie.trailerUri = ${movie.trailerUri}")
            }
        }
    }

    creditResult?.let { movieCreditResult ->
        Logger.i("movieCreditResult = $creditResult")
        movieCreditResult.casts.forEach { cast ->
            if (!cast.profilePath.isNullOrEmpty()) {
                cast.profilePath = TMDB_IMAGE_BASE_URL + cast.profilePath
            }
        }
        movie.casts = movieCreditResult.casts
        movie.crews = movieCreditResult.crews
        movieCreditResult.crews.forEach { crew ->
            when (crew.job) {
                "Director" -> movie.director = crew.name
                "Story" -> movie.writing.add(crew.name)
                else -> {}
            }
        }
    }

    return movie
}
