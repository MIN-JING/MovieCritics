package com.jim.moviecritics.data

data class Score(
    val id: Long,
    val userID: Long,
    val imdbID: String,
    val createdTime: Long,
    val leisure: Float,
    val hit: Float,
    val cast: Float,
    val music: Float,
    val story: Float,
    val average: Float
)
