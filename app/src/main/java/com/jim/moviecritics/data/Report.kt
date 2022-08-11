package com.jim.moviecritics.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Report(
    var id: String = "",
    var userID: String = "",
    var imdbID: String = "",
    var commentID: String = "",
    var createdTime: Timestamp = Timestamp.now(),
    var reason: String = "",
    var message: String = ""
) : Parcelable