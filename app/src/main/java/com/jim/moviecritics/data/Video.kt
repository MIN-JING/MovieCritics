package com.jim.moviecritics.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Video(
    @Json(name = "iso_639_1") val iso639: String,
    @Json(name = "iso_3166_1") val iso3166: String,
    val name: String,
    val key: String,
    val site: String,
    val size: Int,
    val type: String,
    val official: Boolean,
    @Json(name = "published_at") val published: String,
    val id: String
) : Parcelable