package com.jim.moviecritics.data

import com.squareup.moshi.Json


data class SearchResult(
    @Json(name = "status_message")val error: String? = null,
    val page: Int? = null,
    @Json(name = "results") val trends: List<Look>? = null,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int
)
