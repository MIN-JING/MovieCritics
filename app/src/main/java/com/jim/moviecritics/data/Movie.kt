package com.jim.moviecritics.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Movie(
    val id: Long,
    val imdbID: String?,
    val customID: String,
    val title: String,
    val posterUri: String,
    val trailerUri: String,
    val released: String,
    val genre: String,
    val director: String,
    val actors: List<Actor>,
    val overview: String?,
    val country: String,
    val awards: String,
    val rated: String,
    val runTime: Int?,
    val writer: String,
    val ratings: List<Rating>,
    val sales: String,
    val salesTaiwan: String?
) : Parcelable
