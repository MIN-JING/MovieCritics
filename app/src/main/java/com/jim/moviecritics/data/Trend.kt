package com.jim.moviecritics.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Trend(
    @Json(name = "poster_path") var posterUri: String?,
    val adult: Boolean,
    val overview: String,
    @Json(name = "release_date") val releaseDate: String,
    @Json(name = "genre_ids") val genreIds: List<Int>,
    val id: Int,
    @Json(name = "original_title") val originalTitle: String,
    @Json(name = "original_language") val originalLanguage: String,
    val title: String,
    @Json(name = "backdrop_path") val backdrop: String?,
    val popularity: Float,
    @Json(name = "vote_count") val count: Int,
    @Json(name = "vote_average") val average: Float,
    val video: Boolean,
    @Json(name = "media_type") val type: String
) : Parcelable
