package com.jim.moviecritics.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize


@Parcelize
data class Comment(
    var id: String = "",
    val userID: Long = 0L,
    val imdbID: String = "",
    var createdTime: Timestamp = Timestamp.now(),
    var content: String = "",
    val likes: List<Long> = listOf(),
    val dislikes: List<Long> = listOf()
) : Parcelable
