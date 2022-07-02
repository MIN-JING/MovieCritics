package com.jim.moviecritics.data

import android.os.Parcelable
import android.provider.ContactsContract
import kotlinx.parcelize.Parcelize
import com.google.firebase.Timestamp


@Parcelize
data class User(
    var id: Long = 0L,
    var name: String = "",
    var pictureUri: String = "",
    val location: String = "",
    val instagramUri: String = "",
    val twitterUri: String = "",
    val followers: List<Long> = listOf(),
    val followings: List<Long> = listOf(),
    val blocks: List<Long> = listOf(),
    val watched: MutableList<String> = mutableListOf(),
    val liked: MutableList<String> = mutableListOf(),
    val watchlist: MutableList<String> = mutableListOf(),
    var firebaseToken: String = "",
    var firebaseTokenExpiration: Timestamp = Timestamp.now(),
    var signInProvider: String = "",
    var email: String = ""
) : Parcelable
