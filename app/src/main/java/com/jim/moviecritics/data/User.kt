package com.jim.moviecritics.data

data class User(
    val id: Long,
    val name: String,
    val pictureUri: String,
    val location: String,
    val instagramUri: String,
    val twitterUri: String,
    val followers: List<Long>,
    val followings: List<Long>,
    val blocks: List<Long>,
    val favorites: List<String>,
    val downshifts: List<String>
)
