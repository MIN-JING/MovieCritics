package com.jim.moviecritics.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize


@Parcelize
data class PopularMoviesResult(
    @Json(name = "status_message")val error: String? = null,
    val page: Int? = null,
    @Json(name = "results") val homeItems: List<Trends>? = null,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int
) : Parcelable
