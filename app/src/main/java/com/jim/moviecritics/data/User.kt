package com.jim.moviecritics.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    val id: Long = 0L,
    val name: String = "",
    val pictureUri: String = "",
    val location: String = "",
    val instagramUri: String = "",
    val twitterUri: String = "",
    val followers: List<Long> = listOf(),
    val followings: List<Long> = listOf(),
    val blocks: List<Long> = listOf(),
    val watched: MutableList<String> = mutableListOf(),
    val liked: MutableList<String> = mutableListOf(),
    val watchlist: MutableList<String> = mutableListOf()
) : Parcelable
