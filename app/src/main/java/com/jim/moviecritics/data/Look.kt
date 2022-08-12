package com.jim.moviecritics.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Look(
    @Json(name = "poster_path") var posterPath: String?,
    val adult: Boolean?,
    val overview: String?,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "original_title") val originalTitle: String?,
    @Json(name = "genre_ids") val genreIds: List<Int>?,
    val id: Int,
    @Json(name = "media_type") val mediaType: String,
    @Json(name = "original_language") val originalLanguage: String?,
    val title: String?,
    @Json(name = "backdrop_path") val backdrop: String?,
    val popularity: Float?,
    @Json(name = "vote_count") val count: Int?,
    val video: Boolean?,
    @Json(name = "vote_average") val average: Float?,
    // TV
    @Json(name = "first_air_date") val firstAirDate: String?,
    @Json(name = "original_country") val originalCountry: List<String>?,
    val name: String?,
    @Json(name = "original_name") val originalName: String?,
    // Person
    @Json(name = "profile_path") var profilePath: String?,
    @Json(name = "known_for") val knownFor: List<KnownFor>?
) : Parcelable
