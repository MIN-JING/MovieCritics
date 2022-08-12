package com.jim.moviecritics.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Watch(
    var id: String = "",
    var userID: String = "",
    var imdbID: String = "",
    var expiration: Timestamp? = null
) : Parcelable
