package com.jim.moviecritics.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize


@Parcelize
data class Score(
    val id: String = "",
    val userID: Long = 891031L,
    val imdbID: String = "",
    val createdTime: Timestamp = Timestamp.now(),
    val leisure: Float = 0F,
    val hit: Float = 0F,
    val cast: Float = 0F,
    val music: Float = 0F,
    val story: Float = 0F,
    val average: Float = 0F
) : Parcelable
