package com.jim.moviecritics.detail

import com.jim.moviecritics.data.Comment
import com.jim.moviecritics.data.Movie
import com.jim.moviecritics.data.Score
import com.jim.moviecritics.data.User

data class DetailUiState(
    val movie: Movie,
    val userScore: Score? = null,
    val comments: List<Comment> = emptyList(),
    val usersById: Map<String, User> = emptyMap(),
) {
    /** TMDB's average, already scaled to 0–5 by `buildMovie()`, on every axis. */
    val tmdbRatings: List<Float>
        get() = List(RATING_AXES) { movie.voteAverage }

    /**
     * The signed-in user's ratings, or null when they are signed out or have not rated this
     * movie. The repository reports "not rated" as an empty [Score] without an id.
     */
    val userRatings: List<Float>?
        get() = userScore
            ?.takeIf { it.id.isNotEmpty() }
            ?.let { listOf(it.leisure, it.hit, it.cast, it.music, it.story) }

    companion object {
        const val RATING_AXES = 5
        const val MAX_RATING = 5F
    }
}
