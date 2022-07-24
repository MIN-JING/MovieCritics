package com.jim.moviecritics.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rating(
    val source: String,
    val value: String
) : Parcelable
