package com.jim.moviecritics.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comment(
    var id: String = "",
    var userID: String = "",
    var imdbID: String = "",
    var createdTime: Timestamp = Timestamp.now(),
    var content: String = "",
    val likes: MutableList<String> = mutableListOf(),
    val dislikes: MutableList<String> = mutableListOf()
) : Parcelable
