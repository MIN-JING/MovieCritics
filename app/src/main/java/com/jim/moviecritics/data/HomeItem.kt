package com.jim.moviecritics.data

sealed class HomeItem {

    abstract val id: Int

    data class PopularMovie(val trend: Trend) : HomeItem() {
//        data class PopularMovie(val movie: Movie) : HomeItem() {
        override val id: Int
            get() = trend.id
//            get() = movie.imdbID
    }
}
