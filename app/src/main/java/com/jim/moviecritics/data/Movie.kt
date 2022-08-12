package com.jim.moviecritics.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    var id: Int = 0,
    var imdbID: String? = "",
    var title: String = "",
    var posterUri: String? = "",
    var trailerUri: String? = "",
    var released: String = "",
    var genres: List<Genre> = listOf(),
    var director: String? = "",
    var casts: List<Cast> = listOf(),
    var crews: List<Crew> = listOf(),
    var overview: String? = "",
    var country: String? = "",
    var awards: String? = "",
    var runtime: Int? = 0,
    val writing: MutableList<String?> = mutableListOf(),
    var ratings: List<Rating> = listOf(),
    var revenue: Int? = 0,
    var salesTaiwan: String? = "",
    var voteAverage: Float = 0F
) : Parcelable
