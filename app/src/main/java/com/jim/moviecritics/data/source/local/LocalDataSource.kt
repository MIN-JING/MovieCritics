package com.jim.moviecritics.data.source.local


import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.jim.moviecritics.MovieApplication
import com.jim.moviecritics.R
import com.jim.moviecritics.data.*
import com.jim.moviecritics.data.source.ApplicationDataSource
import com.jim.moviecritics.util.Logger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val PATH_SCORES = "scores"
private const val PATH_COMMENTS = "comments"
private const val PATH_USERS = "users"

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

    override suspend fun getScore(imdbID: String, userID: String): Result<Score> {
        TODO("Not yet implemented")
    }

    override fun getLiveScore(imdbID: String, userID: String): MutableLiveData<Score> {
        TODO("Not yet implemented")
    }

    override suspend fun userSignIn(user: User): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(userID: String): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun getComments(imdbID: String): Result<List<Comment>> {
        TODO("Not yet implemented")
    }

    override fun getLiveComments(imdbID: String): MutableLiveData<List<Comment>> {
        TODO("Not yet implemented")
    }

    override suspend fun pushComment(comment: Comment): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(comment: Comment): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushWatchedMovie(imdbID: String, userID: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun removeWatchedMovie(imdbID: String, userID: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushLikedMovie(imdbID: String, userID: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun removeLikedMovie(imdbID: String, userID: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushWatchlistMovie(imdbID: String, userID: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun removeWatchlistMovie(imdbID: String, userID: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushScore(score: Score): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushPopularMovies(trends: List<Trend>): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushMockComment(): Result<Boolean> = suspendCoroutine { continuation ->
        val comment = Comment(
            id = "",
            userID = 200001L,
            imdbID = "tt0343818",
            createdTime = Timestamp.now(),
            content = "Would Google LaMDA chat robot become a human?",
            likes = listOf(300001L, 300002L, 300003L),
            dislikes = listOf(300001L, 300002L, 300003L)
        )

        val comments = FirebaseFirestore.getInstance().collection(PATH_COMMENTS)
        val document = comments.document()
        comment.id = document.id

        document
            .set(comment)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("pushMockComment task.isSuccessful")
                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(MovieApplication.instance.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun pushMockScore(): Result<Boolean> = suspendCoroutine { continuation ->
        val score = Score(
            id = "",
            userID = "2BLJSIq9AiNS9R4egUZqJLb7Stz2",
            imdbID = "tt0343818",
            createdTime = Timestamp.now(),
            leisure = 3.5F,
            hit = 5.0F,
            cast = 2.0F,
            music = 3.5F,
            story = 4.0F,
            average = 3.8F
        )

        val scores = FirebaseFirestore.getInstance().collection(PATH_SCORES)
        val document = scores.document()
        score.id = document.id

        document
            .set(score)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("pushMockScore task.isSuccessful")
                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(MovieApplication.instance.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun pushMockUser(): Result<Boolean> = suspendCoroutine { continuation ->
        val user = User(
            id = "2BLJSIq9AiNS9R4egUZqJLb7Stz2",
            name = "REVIEWER",
            pictureUri = "https://1.bp.blogspot.com/-Tk6O2ne3XbI/Xtt6icgq3WI/AAAAAAABZRU/MAxy4N6fTmIWjBqDVRHg6V2bq8gDY2P9ACNcBGAsYHQ/s400/nebusoku_doctor_man.png",
            location = "Taipei City",
            instagramUri = "https://www.instagram.com/panboknee/",
            twitterUri = "https://twitter.com/totorojack",
            followers = mutableListOf("300001L", "300002L", "300003L"),
            followings = mutableListOf("300001L", "300002L", "300003L"),
            blocks = mutableListOf("400001L", "400002L", "400003L"),
            watched = mutableListOf("tt0343818", "tt5108870", "tt9419884"),
            liked = mutableListOf("tt0343818", "tt5108870", "tt9419884"),
            watchlist = mutableListOf("tt0343818", "tt5108870", "tt9419884"),
            firebaseToken = "eyJhbGc",
            firebaseTokenExpiration = Timestamp.now(),
            signInProvider = "google.com",
            email = "dog@gmail.com")


        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(user.id)
            .set(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("pushMockUser task.isSuccessful")
                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(MovieApplication.instance.getString(R.string.you_know_nothing)))
                }
            }
    }

//    override fun getGoogleSignInClient(): GoogleSignInClient {
//
//        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.server_client_id))
//            .requestEmail()
//            .build()
//
//        val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
//    }

}