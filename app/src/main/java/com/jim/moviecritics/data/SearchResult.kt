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

        looks?.let {
            Logger.i("looksList = $looks")

            for (look in it) {

                when (look.mediaType) {
                    "movie" -> {
                        if (look.posterPath != null) {
                            look.posterPath = "https://image.tmdb.org/t/p/w185" + look.posterPath
                        }
                        items.add(LookItem.LookMovie(look))
                        Logger.i("movie = $look")
                    }
                    "tv" -> {
                        if (look.posterPath != null) {
                            look.posterPath = "https://image.tmdb.org/t/p/w185" + look.posterPath
                        }
                        items.add(LookItem.LookTelevision(look))
                        Logger.i("tv = $look")
                    }
                    "person" -> {
                        if (look.profilePath != null) {
                            look.profilePath = "https://image.tmdb.org/t/p/w185" + look.profilePath
                        }
                        if (look.knownFor != null) {
                            for (knownFor in look.knownFor) {
                                if (knownFor.posterPath != null) {
                                    knownFor.posterPath = "https://image.tmdb.org/t/p/w185" + knownFor.posterPath
                                }
                                if (knownFor.backdropPath != null) {
                                    knownFor.backdropPath = "https://image.tmdb.org/t/p/w185" + knownFor.backdropPath
                                }
                            }
                        }
                        items.add(LookItem.LookPerson(look))
                        Logger.i("person = $look")
                    }
                    else -> { Logger.i("mediaType unknown data = $look") }
                }
            }
            Logger.i("items = $items")
        }
        return items
    }
}
