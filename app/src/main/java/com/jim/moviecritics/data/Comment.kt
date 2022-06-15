package com.jim.moviecritics.data

data class Comment(
    val id: Long,
    val userID: Long,
    val imdbID: String,
    val createdTime: Long,
    val content: Long,
    val likes: List<Long>,
    val dislikes: List<Long>
)
