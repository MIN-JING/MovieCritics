package com.jim.moviecritics.data.source.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.jim.moviecritics.data.*
import com.jim.moviecritics.data.source.ApplicationDataSource

class LocalDataSource(val context: Context) : ApplicationDataSource {
    override suspend fun getPopularMovies(): Result<List<HomeItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMovieDetail(id: Int): Result<MovieDetailResult> {
        TODO("Not yet implemented")
    }

    override suspend fun getMovieCredit(id: Int): Result<CreditResult> {
        TODO("Not yet implemented")
    }

    override suspend fun getSearchMulti(queryKey: String): Result<List<LookItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun getScores(imdbID: String): Result<List<Score>> {
        TODO("Not yet implemented")
    }

    override suspend fun getScore(imdbID: String, userID: Long): Result<Score> {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(userID: Long): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun getComments(): Result<List<Comment>> {
        TODO("Not yet implemented")
    }

    override fun getLiveComments(): MutableLiveData<List<Comment>> {
        TODO("Not yet implemented")
    }

    override suspend fun comment(comment: Comment): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(comment: Comment): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun pushMockComment(): Comment {
        return Comment(
            id = "",
            userID = 891031L,
            imdbID = "tt0343818",
            createdTime = Timestamp.now(),
            content = "Would Google LaMDA chat robot become a human?",
            likes = listOf(12345, 45678, 89012),
            dislikes = listOf(12345, 45678, 89012)
        )
    }

    override fun pushMockScore(): Score {
        return Score(
            id = "",
            userID = 891031L,
            imdbID = "tt0343818",
            createdTime = Timestamp.now(),
            leisure = 3.5F,
            hit = 2.5F,
            cast = 2.0F,
            music = 3.5F,
            story = 4.0F,
            average = 3.1F
        )
    }

    override suspend fun pushWatchedMovie(imdbID: String, userID: Long): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun removeWatchedMovie(imdbID: String, userID: Long): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushLikedMovie(imdbID: String, userID: Long): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun removeLikedMovie(imdbID: String, userID: Long): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushWatchlistMovie(imdbID: String, userID: Long): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun removeWatchlistMovie(imdbID: String, userID: Long): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushScore(score: Score): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushPopularMovies(pushTrend: PushTrend): Result<Boolean> {
        TODO("Not yet implemented")
    }
}