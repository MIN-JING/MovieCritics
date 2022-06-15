package com.jim.moviecritics.data

import com.google.firebase.Timestamp

data class Score(
    val id: Long,
    val userID: Long,
    val imdbID: String,
    val createdTime: Timestamp,
    val leisure: Double,
    val hit: Double,
    val cast: Double,
    val music: Double,
    val story: Double,
    val average: Double
)
