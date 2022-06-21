package com.jim.moviecritics.data

import android.os.Parcelable
import com.jim.moviecritics.util.Logger
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize


@Parcelize
data class PopularMoviesResult(
    @Json(name = "status_message")val error: String? = null,
    val page: Int? = null,
    @Json(name = "results") val trends: List<Trend>? = null,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int
) : Parcelable {

    fun toHomeItems(): List<HomeItem> {

        val items = mutableListOf<HomeItem>()
//        val items = mutableListOf<Trends>()

        trends?.let {
            Logger.i("trendsList = $trends")

            for (trend in it) {
                Logger.i("trend = $trend")
                trend.posterUri = "https://image.tmdb.org/t/p/w185" + trend.posterUri
                Logger.i("trend.posterUri = ${trend.posterUri}")
                items.add(HomeItem.PopularMovie(trend))
            }
            Logger.i("items = $items")
        }
        return items
    }
}
