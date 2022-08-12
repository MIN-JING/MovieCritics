package com.jim.moviecritics.data

import android.os.Parcelable
import com.jim.moviecritics.util.Logger
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchResult(
    @Json(name = "status_message")val error: String?,
    val page: Int?,
    @Json(name = "results") val looks: List<Look>?,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int
) : Parcelable {

    fun toLookItems(): List<LookItem> {
        val items = mutableListOf<LookItem>()
        looks?.let { lookList ->
            lookList.forEach { look ->
                when (look.mediaType) {
                    "movie" -> {
                        if (!look.posterPath.isNullOrEmpty()) {
                            look.posterPath = "https://image.tmdb.org/t/p/w185" + look.posterPath
                        }
                        items.add(LookItem.LookMovie(look))
                    }
                    "tv" -> {
                        if (!look.posterPath.isNullOrEmpty()) {
                            look.posterPath = "https://image.tmdb.org/t/p/w185" + look.posterPath
                        }
                        items.add(LookItem.LookTelevision(look))
                    }
                    "person" -> {
                        if (!look.profilePath.isNullOrEmpty()) {
                            look.profilePath = "https://image.tmdb.org/t/p/w185" + look.profilePath
                        }
                        if (!look.knownFor.isNullOrEmpty()) {
                            look.knownFor.forEach { knownFor ->
                                if (!knownFor.posterPath.isNullOrEmpty()) {
                                    knownFor.posterPath =
                                        "https://image.tmdb.org/t/p/w185" + knownFor.posterPath
                                }
                                if (!knownFor.backdropPath.isNullOrEmpty()) {
                                    knownFor.backdropPath =
                                        "https://image.tmdb.org/t/p/w185" + knownFor.backdropPath
                                }
                            }
                        }
                        items.add(LookItem.LookPerson(look))
                    }
                    else -> { Logger.i("mediaType unknown data = $look") }
                }
                items.sortBy { items -> items.order }
            }
        }
        return items
    }
}
