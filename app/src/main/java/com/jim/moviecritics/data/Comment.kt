package com.jim.moviecritics.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize


@Parcelize
data class Comment(
    var id: String = "",
    val userID: Long = 891031L,
    val imdbID: String = "",
    var createdTime: Timestamp = Timestamp.now(),
    val content: String = "",
    val likes: List<Long> = listOf(0L),
    val dislikes: List<Long> = listOf(0L)
) : Parcelable
