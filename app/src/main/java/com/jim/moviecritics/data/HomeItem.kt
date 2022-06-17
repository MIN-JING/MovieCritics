package com.jim.moviecritics.data

sealed class HomeItem {

    abstract val id: String

    data class PopularMovie(val movie: Movie) : HomeItem() {
        override val id: String
            get() = movie.imdbID
    }
}
