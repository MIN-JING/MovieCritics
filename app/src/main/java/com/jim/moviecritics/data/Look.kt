package com.jim.moviecritics.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Look(
    @Json(name = "poster_path") var posterPath: String? = null,
    val adult: Boolean? = null,
    val overview: String? = null,
    @Json(name = "release_date") val releaseDate: String? = null,
    @Json(name = "original_title") val originalTitle: String? = null,
    @Json(name = "genre_ids") val genreIds: List<Int>? = null,
    val id: Int = -1,
    @Json(name = "media_type") val mediaType: String = "",
    @Json(name = "original_language") val originalLanguage: String? = null,
    val title: String? = null,
    @Json(name = "backdrop_path") val backdrop: String? = null,
    val popularity: Float? = null,
    @Json(name = "vote_count") val count: Int? = null,
    val video: Boolean? = null,
    @Json(name = "vote_average") val average: Float? = null,
    // TV
    @Json(name = "first_air_date") val firstAirDate: String? = null,
    @Json(name = "original_country") val originalCountry: List<String>? = null,
    val name: String? = null,
    @Json(name = "original_name") val originalName: String? = null,
    // Person
    @Json(name = "profile_path") var profilePath: String? = null,
    @Json(name = "known_for") val knownFor: List<KnownFor>? = null
) : Parcelable
