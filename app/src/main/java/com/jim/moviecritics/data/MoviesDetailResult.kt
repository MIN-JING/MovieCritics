package com.jim.moviecritics.data

import com.squareup.moshi.Json

data class MoviesDetailResult(
    @Json(name = "status_message")val error: String?,
    val adult: Boolean,
    @Json(name = "backdrop_path") val backdrop: String?,
    @Json(name = "belongs_to_collection") val belongsTo: List<Any>?,
    val budget: Int,
    val genres: List<Genre>,
    val homepage: String?,
    val id: Int,
    @Json(name = "imdb_id") val imdbID: String?,
    @Json(name = "original_language") val originalLanguage: String,
    @Json(name = "original_title") val originalTitle: String,
    val overview: String?,
    val popularity: Double,
    @Json(name = "poster_path") var posterUri: String?,
    @Json(name = "release_date") val releaseDate: String,
    val revenue: Int,
    val runTime: Int?,
    val status: String,
    val tagline: String?,
    val title: String,
    val video: Boolean,
    @Json(name = "vote_average") val average: Double,
    @Json(name = "vote_count") val count: Int,
)
