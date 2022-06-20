package com.jim.moviecritics.data

import com.google.firebase.Timestamp

data class Score(
    val id: Long,
    val userID: Long,
    val imdbID: String,
    val createdTime: Timestamp,
    val leisure: Float,
    val hit: Float,
    val cast: Float,
    val music: Float,
    val story: Float,
    val average: Float
)
