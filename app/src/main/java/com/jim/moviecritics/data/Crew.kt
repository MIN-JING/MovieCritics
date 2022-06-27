package com.jim.moviecritics.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Crew(
    val adult: Boolean,
    val gender: Int?,
    val id: Int,
    val name: String,
    @Json(name = "original_name")val originalName: String,
    val popularity: Float,
    @Json(name = "profile_path")val profilePath: String?,
    @Json(name = "credit_id")val creditId: String,
    val job: String
) : Parcelable
