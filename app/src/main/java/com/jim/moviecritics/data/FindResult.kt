package com.jim.moviecritics.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class FindResult(
    @Json(name = "status_message")val error: String?,
    @Json(name = "movie_results") val finds: List<Find>?
) : Parcelable