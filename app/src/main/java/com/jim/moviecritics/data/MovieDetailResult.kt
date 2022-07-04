package com.jim.moviecritics.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieDetailResult(
    @Json(name = "status_message")val error: String?,
    val adult: Boolean,
    @Json(name = "backdrop_path") val backdrop: String?,
    val budget: Int,
    val genres: List<Genre>,
    val homepage: String?,
    val id: Int,
    @Json(name = "imdb_id") val imdbID: String?,
    @Json(name = "original_language") val originalLanguage: String,
    @Json(name = "original_title") val originalTitle: String,
    val overview: String?,
    val popularity: Float,
    @Json(name = "poster_path") var posterUri: String?,
    @Json(name = "release_date") val releaseDate: String,
    val revenue: Int,
    val runtime: Int?,
    val status: String,
    val tagline: String?,
    val title: String,
    val video: Boolean,
    @Json(name = "vote_average") val average: Float,
    @Json(name = "vote_count") val count: Int,
) : Parcelable
