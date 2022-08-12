package com.jim.moviecritics.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class PopularMoviesResult(
    @Json(name = "status_message")val error: String?,
    val page: Int?,
    @Json(name = "results") val trends: List<Trend>?,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int
) : Parcelable {

    fun toHomeItems(): List<HomeItem> {
        val items = mutableListOf<HomeItem>()
        trends?.let { trendList ->
            trendList.forEach { trend ->
                if (!trend.posterUri.isNullOrEmpty()) {
                    trend.posterUri = "https://image.tmdb.org/t/p/w185" + trend.posterUri
                }
                items.add(HomeItem.PopularMovie(trend))
            }
        }
        return items
    }
}
