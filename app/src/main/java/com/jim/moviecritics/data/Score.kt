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
    var leisure: Float = 0F,
    var hit: Float = 0F,
    var cast: Float = 0F,
    var music: Float = 0F,
    var story: Float = 0F,
    var average: Float = 0F
) : Parcelable
