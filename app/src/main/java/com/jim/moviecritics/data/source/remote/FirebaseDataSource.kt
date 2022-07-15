package com.jim.moviecritics.data.source.remote

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jim.moviecritics.MovieApplication
import com.jim.moviecritics.R
import com.jim.moviecritics.data.source.ApplicationDataSource
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.jim.moviecritics.data.*
import com.jim.moviecritics.util.Logger


/**
 * Implementation of the Application source that from network.
 */
object FirebaseDataSource : ApplicationDataSource {

    private const val PATH_SCORES = "scores"
    private const val PATH_COMMENTS = "comments"
    private const val PATH_POPULAR_MOVIES = "popularMovies"
    private const val PATH_USERS = "users"
    private const val PATH_WATCHLIST = "watchlist"
    private const val PATH_REPORTS = "reports"
    private const val FIELD_IMDB_ID = "imdbID"
    private const val FIELD_USER_ID = "userID"
    private const val FIELD_WATCHED = "watched"
    private const val FIELD_LIKED = "liked"
    private const val FIELD_CREATED_TIME = "createdTime"
    private const val FIELD_ID = "id"
    private const val FIELD_FIREBASE_TOKEN = "firebaseToken"
    private const val FIELD_EXPIRATION = "expiration"
    private const val FIELD_BLOCKS = "blocks"


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

    override suspend fun getFind(imdbID: String): Result<FindResult> {
        TODO("Not yet implemented")
    }

    override fun getLiveWatchList(imdbID: String, userID: String): MutableLiveData<Watch> {
        val liveData = MutableLiveData<Watch>()

        FirebaseFirestore.getInstance()
            .collection(PATH_WATCHLIST)
            .whereEqualTo(FIELD_IMDB_ID, imdbID)
            .whereEqualTo(FIELD_USER_ID, userID)
            .addSnapshotListener { snapshot, exception ->
                Logger.i("getLiveWatchList addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                if (snapshot != null) {
                    if (snapshot.size() >= 1) {
                        val list = mutableListOf<Watch>()
                        snapshot.forEach { document ->
                            Logger.d(document.id + " => " + document.data)
                            val watch = document.toObject(Watch::class.java)
                            list.add(watch)
                        }
                        Logger.d( list.first().id + " => " + list.first())
                        liveData.value = list.first()
                        Logger.i("live watchList snapshot.size() >= 1 liveData.value = ${liveData.value}")
                        Logger.i("live watchList snapshot.size() >= 1 liveData = $liveData")
                    } else {
                        Logger.w("[${this::class.simpleName}] getLiveWatchList task.result.size < 1")
                        liveData.value = Watch()
                        Logger.i("live watchList snapshot.size() < 1 liveData = ${liveData.value}")
                    }
                } else {
                    Logger.w("[${this::class.simpleName}] getLiveWatchList snapshot == null")
                }
            }
        return liveData
    }

    override fun getLiveWatchListByUser(userID: String): MutableLiveData<List<Watch>> {
        val liveData = MutableLiveData<List<Watch>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_WATCHLIST)
            .whereEqualTo(FIELD_USER_ID, userID)
            .addSnapshotListener { snapshot, exception ->
                Logger.i("getLiveWatchListByUser addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                if (snapshot != null) {
                    if (snapshot.size() >= 1) {
                        val list = mutableListOf<Watch>()
                        snapshot.forEach { document ->
                            Logger.d(document.id + " => " + document.data)
                            val watch = document.toObject(Watch::class.java)
                            list.add(watch)
                        }
                        liveData.value = list
                        Logger.i("live watchList By User snapshot.size() >= 1 liveData.value = ${liveData.value}")
                        Logger.i("live watchList By User snapshot.size() >= 1 liveData = $liveData")
                    } else {
                        Logger.w("[${this::class.simpleName}] getLiveWatchListByUser task.result.size < 1")
                        liveData.value = listOf(Watch())
                        Logger.i("live watchList By User snapshot.size() < 1 liveData = ${liveData.value}")
                    }
                } else {
                    Logger.w("[${this::class.simpleName}] getLiveWatchListByUser snapshot == null")
                }
            }
        return liveData
    }

    override suspend fun pushMultiWatchListExpiration(imdbID: String, userID: String, expiration: Timestamp): Result<Boolean> = suspendCoroutine { continuation ->
        val itemRef = FirebaseFirestore.getInstance().collection(PATH_WATCHLIST)
        val query =  itemRef.whereEqualTo(FIELD_IMDB_ID, imdbID).whereEqualTo(FIELD_USER_ID, userID)

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Logger.w("[${this::class.simpleName}] pushWatchListExpiration get() task.result.size = ${task.result.size()}")
                for (document in task.result) {
                    Logger.i("Update: ${document.id} => ${document.data}")
                    itemRef.document(document.id).update(FIELD_EXPIRATION, expiration)
                        .addOnSuccessListener {
                            Logger.i("pushWatchListExpiration update() Success")
                            continuation.resume(Result.Success(true))
                        }.addOnFailureListener {
                            Logger.w("pushWatchListExpiration update() Failure, Error = ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                }
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

    override suspend fun pushSingleWatchListExpiration(watch: Watch): Result<Boolean>  = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_WATCHLIST)
            .document(watch.id)
            .update(FIELD_EXPIRATION, watch.expiration)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("pushWatchListExpiration task.isSuccessful")
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

    override suspend fun getScores(imdbID: String): Result<List<Score>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_SCORES)
            .whereEqualTo(FIELD_IMDB_ID, imdbID)
            .orderBy(FIELD_CREATED_TIME, Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result.size() >= 1) {
                        Logger.w("[${this::class.simpleName}] getScores task.result.size >= 1")
                        Logger.d( task.result.first().id + " => " + task.result.first().data)
                        val list = mutableListOf<Score>()
                        for (document in task.result) {
                            Logger.d( document.id + " => " + document.data)
                            val score = document.toObject(Score::class.java)
                            list.add(score)
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        Logger.w("[${this::class.simpleName}] getScores task.result.size < 1")
                        val list = listOf(Score(imdbID = imdbID))
                        continuation.resume(Result.Success(list))
                    }
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

    override suspend fun getScore(imdbID: String, userID: String): Result<Score> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_SCORES)
            .whereEqualTo(FIELD_IMDB_ID, imdbID)
            .whereEqualTo(FIELD_USER_ID, userID)
            .orderBy(FIELD_CREATED_TIME, Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result.size() >= 1) {
                        Logger.w("[${this::class.simpleName}] getScore task.result.size >= 1")
                        Logger.d( task.result.first().id + " => " + task.result.first().data)
                        val item = task.result.first().toObject(Score::class.java)
                        continuation.resume(Result.Success(item))
                    } else {
                        Logger.w("[${this::class.simpleName}] getScore task.result.size < 1")
                        val item = Score(imdbID = imdbID, userID = userID)
                        continuation.resume(Result.Success(item))
                    }
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

    override fun getLiveScore(imdbID: String, userID: String): MutableLiveData<Score> {
        val liveData = MutableLiveData<Score>()

        FirebaseFirestore.getInstance()
            .collection(PATH_SCORES)
            .whereEqualTo(FIELD_IMDB_ID, imdbID)
            .whereEqualTo(FIELD_USER_ID, userID)
            .orderBy(FIELD_CREATED_TIME, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                Logger.i("getLiveScore addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                if (snapshot != null) {
                    if (snapshot.size() >= 1) {
                        val list = mutableListOf<Score>()
                        snapshot.forEach { document ->
                            Logger.d(document.id + " => " + document.data)
                            val score = document.toObject(Score::class.java)
                            list.add(score)
                        }
                        Logger.d( list.first().id + " => " + list.first())
                        liveData.value = list.first()
                        Logger.i("live score snapshot.size() >= 1 liveData.value = ${liveData.value}")
                        Logger.i("live score snapshot.size() >= 1 liveData = $liveData")
                    } else {
                        Logger.w("[${this::class.simpleName}] getLiveScore task.result.size < 1")
                        liveData.value = Score(imdbID = imdbID, userID = userID)
                        Logger.i("live score snapshot.size() < 1 liveData = ${liveData.value}")
                    }
                } else {
                    Logger.w("[${this::class.simpleName}] getLiveScore snapshot == null")
                }
            }
        return liveData
    }

    override suspend fun pushUserInfo(user: User): Result<Boolean> = suspendCoroutine { continuation ->

        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(user.id)
            .set(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("pushScore task.isSuccessful")
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

    override suspend fun getUserByToken(token: String): Result<User> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .whereEqualTo(FIELD_FIREBASE_TOKEN, token)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result.size() >= 1) {
                        Logger.w("[${this::class.simpleName}] getUserByToken task.result.size >= 1")
                        Logger.d( task.result.first().id + " => " + task.result.first().data)
                        val item = task.result.first().toObject(User::class.java)
                        continuation.resume(Result.Success(item))
                    } else {
                        Logger.w("[${this::class.simpleName}] getUserByToken task.result.size < 1")
                        val item = User(firebaseToken = "token")
                        continuation.resume(Result.Success(item))
                    }
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

    override suspend fun getUserById(id: String): Result<User?> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .whereEqualTo(FIELD_ID, id)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result.size() >= 1) {
                        Logger.w("[${this::class.simpleName}] getUserById task.result.size >= 1")
                        Logger.d( task.result.first().id + " => " + task.result.first().data)
                        val item = task.result.first().toObject(User::class.java)
                        continuation.resume(Result.Success(item))
                    } else {
                        Logger.w("[${this::class.simpleName}] getUserById task.result.size < 1")
                        val item = User(id = id)
                        continuation.resume(Result.Success(item))
                    }
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

    override suspend fun getUsersByIdList(idList: List<String>): Result<List<User>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .whereIn(FIELD_ID, idList)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result.size() >= 1) {
                        Logger.w("[${this::class.simpleName}] getUsersByIdList task.result.size >= 1")
                        Logger.d( "First element is " + task.result.first().id + " => " + task.result.first().data)
                        val list = mutableListOf<User>()
                        for (document in task.result) {
                            Logger.d( document.id + " => " + document.data)
                            val user = document.toObject(User::class.java)
                            list.add(user)
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        Logger.w("[${this::class.simpleName}] getUserById task.result.size < 1")
                    }
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

    override suspend fun getComments(imdbID: String): Result<List<Comment>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_COMMENTS)
            .whereEqualTo(FIELD_IMDB_ID, imdbID)
            .orderBy(FIELD_CREATED_TIME, Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result.size() >= 1) {
                        Logger.w("[${this::class.simpleName}] getComments task.result.size >= 1")
                        Logger.d( task.result.first().id + " => " + task.result.first().data)
                        val list = mutableListOf<Comment>()
                        for (document in task.result) {
                            Logger.d( document.id + " => " + document.data)
                            val comment = document.toObject(Comment::class.java)
                            list.add(comment)
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        Logger.w("[${this::class.simpleName}] getComments task.result.size < 1")
                    }
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

    override fun getLiveComments(imdbID: String): MutableLiveData<List<Comment>> {
        val liveData = MutableLiveData<List<Comment>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_COMMENTS)
            .whereEqualTo(FIELD_IMDB_ID, imdbID)
            .orderBy(FIELD_CREATED_TIME, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                Logger.i("getLiveComments addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                if (snapshot != null) {
                    if (snapshot.size() >=1 ) {
                        val list = mutableListOf<Comment>()
                        snapshot.forEach { document ->
                            Logger.d(document.id + " => " + document.data)
                            val comment = document.toObject(Comment::class.java)
                            list.add(comment)
                        }
                        liveData.value = list
                    } else {
                        Logger.w("[${this::class.simpleName}] getLiveComments task.result.size < 1")
                    }
                } else {
                    Logger.w("[${this::class.simpleName}] getLiveComments snapshot == null")
                }
            }
        return liveData
    }

    override fun getLiveCommentsExcludeBlocks(imdbID: String, blocks: List<String>): MutableLiveData<List<Comment>> {
        val liveData = MutableLiveData<List<Comment>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_COMMENTS)
            .whereEqualTo(FIELD_IMDB_ID, imdbID)
            .whereNotIn(FIELD_USER_ID, blocks)
            .orderBy(FIELD_USER_ID, Query.Direction.DESCENDING)
            .orderBy(FIELD_CREATED_TIME, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                Logger.i("getLiveCommentsExcludeBlocks addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                if (snapshot != null) {
                    if (snapshot.size() >=1 ) {
                        val list = mutableListOf<Comment>()
                        snapshot.forEach { document ->
                            Logger.d(document.id + " => " + document.data)
                            val comment = document.toObject(Comment::class.java)
                            list.add(comment)
                        }
                        liveData.value = list
                    } else {
                        Logger.w("[${this::class.simpleName}] getLiveCommentsExcludeBlocks task.result.size < 1")
                    }
                } else {
                    Logger.w("[${this::class.simpleName}] getLiveCommentsExcludeBlocks snapshot == null")
                }
            }
        return liveData
    }

    override fun getLivePersonalComments(userID: String): MutableLiveData<List<Comment>> {
        val liveData = MutableLiveData<List<Comment>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_COMMENTS)
            .whereEqualTo(FIELD_USER_ID, userID)
            .orderBy(FIELD_CREATED_TIME, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                Logger.i("getLivePersonalComments addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                if (snapshot != null) {
                    if (snapshot.size() >= 1) {
                        val list = mutableListOf<Comment>()
                        snapshot.forEach { document ->
                            Logger.d(document.id + " => " + document.data)
                            val comment = document.toObject(Comment::class.java)
                            list.add(comment)
                        }
                        liveData.value = list
                    } else {
                        Logger.w("[${this::class.simpleName}] getLivePersonalComments task.result.size < 1")
                    }
                } else {
                    Logger.w("[${this::class.simpleName}] getLivePersonalComments snapshot == null")
                }
            }
        return liveData
    }

    override suspend fun pushComment(comment: Comment): Result<Boolean> = suspendCoroutine { continuation ->
        val comments = FirebaseFirestore.getInstance().collection(PATH_COMMENTS)
        val document = comments.document()

        comment.id = document.id
        comment.createdTime = Timestamp.now()

        document
            .set(comment)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
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

    override suspend fun delete(comment: Comment): Result<Boolean> = suspendCoroutine { continuation ->

        when (comment.userID) {
            "12344L" -> {
                continuation.resume(Result.Fail("You know nothing!! ${comment.userID}"))
            }
            else -> {
                FirebaseFirestore.getInstance()
                    .collection(PATH_COMMENTS)
                    .document(comment.id)
                    .delete()
                    .addOnSuccessListener {
                        Logger.i("Delete: $comment")
                        continuation.resume(Result.Success(true))
                    }.addOnFailureListener {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                    }
            }
        }
    }

    override fun getLivePersonalFavorites(userID: String): MutableLiveData<List<String>> {
        val liveData = MutableLiveData<List<String>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .whereEqualTo(FIELD_ID, userID)
            .addSnapshotListener { snapshot, exception ->
                Logger.i("getLivePersonalFavorites addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                if (snapshot != null) {
                    if (snapshot.size() >= 1) {
                        val list = mutableListOf<User>()
                        snapshot.forEach { document ->
                            Logger.d(document.id + " => " + document.data)
                            val user= document.toObject(User::class.java)
                            list.add(user)
                        }
                        liveData.value = list.first().liked
                    } else {
                        Logger.w("[${this::class.simpleName}] getLivePersonalFavorites task.result.size < 1")
                    }
                } else {
                    Logger.w("[${this::class.simpleName}] getLivePersonalFavorites snapshot == null")
                }
            }
        return liveData
    }

    override suspend fun pushWatchedMovie(imdbID: String, userID: String): Result<Boolean> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(userID)
            .update(FIELD_WATCHED, FieldValue.arrayUnion(imdbID))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("pushWatchedMovie task.isSuccessful")
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

    override suspend fun removeWatchedMovie(imdbID: String, userID: String): Result<Boolean> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(userID)
            .update(FIELD_WATCHED, FieldValue.arrayRemove(imdbID))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("removeWatchedMovie task.isSuccessful")
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

    override suspend fun pushLikedMovie(imdbID: String, userID: String): Result<Boolean> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(userID)
            .update(FIELD_LIKED, FieldValue.arrayUnion(imdbID))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("pushLikedMovie task.isSuccessful")
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

    override suspend fun removeLikedMovie(imdbID: String, userID: String): Result<Boolean> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(userID)
            .update(FIELD_LIKED, FieldValue.arrayRemove(imdbID))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("removeLikedMovie task.isSuccessful")
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

    override suspend fun pushWatchlistMovie(watch: Watch): Result<Boolean> = suspendCoroutine { continuation ->
        val watchList = FirebaseFirestore.getInstance().collection(PATH_WATCHLIST)
        val document = watchList.document()

        watch.id = document.id

        document
            .set(watch)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("pushWatchlistMovie task.isSuccessful")
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

    override suspend fun removeWatchlistMovie(imdbID: String, userID: String): Result<Boolean> = suspendCoroutine { continuation ->
        val itemRef = FirebaseFirestore.getInstance().collection(PATH_WATCHLIST)
        val query =  itemRef.whereEqualTo(FIELD_IMDB_ID, imdbID).whereEqualTo(FIELD_USER_ID, userID)

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Logger.w("[${this::class.simpleName}] removeWatchlistMovie get() task.result.size = ${task.result.size()}")
                for (document in task.result) {
                    Logger.i("Delete: ${document.id} => ${document.data}")
                    itemRef.document(document.id).delete()
                        .addOnSuccessListener {
                            Logger.i("removeWatchlistMovie delete() Success")
                            continuation.resume(Result.Success(true))
                        }.addOnFailureListener {
                            Logger.w("removeWatchlistMovie delete() Failure, Error = ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                }
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

    override suspend fun pushScore(score: Score): Result<Boolean>  = suspendCoroutine { continuation ->
        val scores = FirebaseFirestore.getInstance().collection(PATH_SCORES)
        val document = scores.document()

        score.id = document.id

        document
            .set(score)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("pushScore task.isSuccessful")
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

    override suspend fun pushReport(report: Report): Result<Boolean>  = suspendCoroutine { continuation ->
        val reports = FirebaseFirestore.getInstance().collection(PATH_REPORTS)
        val document = reports.document()

        report.id = document.id

        document
            .set(report)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("pushReport task.isSuccessful")
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

    override suspend fun pushBlockUser(userID: String, blockedID: String): Result<Boolean> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .document(userID)
            .update(FIELD_BLOCKS, FieldValue.arrayUnion(blockedID))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("pushBlockUser task.isSuccessful")
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

    override suspend fun pushPopularMovies(trends: List<Trend>): Result<Boolean>  = suspendCoroutine { continuation ->
        val popularMovies = FirebaseFirestore.getInstance().collection(PATH_POPULAR_MOVIES)

        for (trend in trends) {
            popularMovies
                .document(trend.id.toString())
                .set(trend)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Logger.i("pushPopularMovies task.isSuccessful")
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
    }

    override suspend fun pushMockComment(): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushMockScore(): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pushMockUser(): Result<Boolean> {
        TODO("Not yet implemented")
    }
}