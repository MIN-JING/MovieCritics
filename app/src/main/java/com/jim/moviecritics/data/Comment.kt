package com.jim.moviecritics.data

import com.google.firebase.Timestamp

data class Comment(
    var id: String,
    val userID: Long,
    val imdbID: String,
    var createdTime: Timestamp,
    val content: String,
    val likes: List<Long>,
    val dislikes: List<Long>
)
