package com.jim.moviecritics.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreditResult(
    @Json(name = "status_message")val error: String?,
    val id: Int,
    @Json(name = "cast")val casts: List<Cast>,
    @Json(name = "crew")val crews: List<Crew>,
) : Parcelable