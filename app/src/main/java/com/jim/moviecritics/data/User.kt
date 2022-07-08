package com.jim.moviecritics.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.firebase.Timestamp


@Parcelize
data class User(
    var id: String = "",
    var name: String = "",
    var pictureUri: String = "",
    val location: String = "",
    val instagramUri: String = "",
    val twitterUri: String = "",
    val followers: List<String> = mutableListOf(),
    val followings: List<String> = mutableListOf(),
    val blocks: List<String> = mutableListOf(),
    val watched: MutableList<String> = mutableListOf(),
    val liked: MutableList<String> = mutableListOf(),
    val watchlist: MutableList<String> = mutableListOf(),
    var firebaseToken: String = "",
    var firebaseTokenExpiration: Timestamp = Timestamp.now(),
    var signInProvider: String = "",
    var email: String = "",
) : Parcelable
