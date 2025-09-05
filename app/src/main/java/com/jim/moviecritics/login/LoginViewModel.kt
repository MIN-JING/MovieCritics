package com.jim.moviecritics.login

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.jim.moviecritics.R
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.User
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

class LoginViewModel(private val repository: Repository) : ViewModel() {

    companion object {
        const val FIREBASE_LOG_IN_FIRST = 0x11
        const val FIREBASE_LOG_IN_EVER = 0x12
        const val NO_ONE_KNOWS = 0x21
    }

    var user = User()

    private val _liveUser = MutableLiveData<User>()

    val liveUser: LiveData<User>
        get() = _liveUser

    private val _statusLogIn = MutableLiveData<Int>()

    val statusLogIn: LiveData<Int>
        get() = _statusLogIn

    // Handle navigation to login success
    private val _navigateToLoginSuccess = MutableLiveData<User>()

    val navigateToLoginSuccess: LiveData<User>
        get() = _navigateToLoginSuccess

    // Handle leave login
    private val _leave = MutableLiveData<Boolean?>()

    val leave: LiveData<Boolean?>
        get() = _leave

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]$this")
        Logger.i("------------------------------------")
    }

    fun leave() {
        _leave.value = true
    }

    fun onLeaveCompleted() {
        _leave.value = null
    }

    fun signInWithGoogle(activity: Activity) {
        viewModelScope.launch {
            try {
                val webClientId = activity.getString(R.string.default_web_client_id)

                // Optional but recommended if you verify on your server:
                val nonce: String? = null // or your generated nonce

                val option = GetSignInWithGoogleOption.Builder(webClientId)
                    .setNonce(nonce)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(option)
                    .build()

                val cm = CredentialManager.create(activity)
                val resp = cm.getCredential(activity, request)
                val cred = resp.credential

                if (cred is CustomCredential &&
                    cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val tokenCredential = GoogleIdTokenCredential.createFrom(cred.data)
                    val idToken = tokenCredential.idToken
                    updateUserInfo(tokenCredential)
                    // your existing coroutine-friendly Firebase sign-in:
                    firebaseAuthWithGoogleSuspend(idToken, user).onFailure { e ->
                        Logger.w("Firebase sign-in failed: $e")
                        _statusLogIn.postValue(NO_ONE_KNOWS)
                    }
                    // you can also call updateUserInfo(google) if you need email, displayName, etc.
                } else {
                    Logger.w("Unexpected credential type: ${cred.javaClass.simpleName}")
                    _statusLogIn.postValue(NO_ONE_KNOWS)
                }
            } catch (t: Throwable) {
                Logger.w("GetSignInWithGoogleOption error: ${t.message}")
                _statusLogIn.postValue(NO_ONE_KNOWS)
            }
        }
    }

    private fun updateUserInfo(credential: GoogleIdTokenCredential) {
        user.id = credential.id
        user.name = credential.givenName + " " + credential.familyName
        user.email = ""
        user.pictureUri = credential.profilePictureUri?.toString() ?: ""
        Logger.i(
            "Google ID = ${user.id}, Name = ${user.name}, Email = ${user.email}, " +
                    "Picture URI = ${user.pictureUri}"
        )
    }

    private suspend fun firebaseAuthWithGoogleSuspend(
        idToken: String,
        user: User
    ): kotlin.Result<User> = runCatching {
        val firebaseAuth = Firebase.auth

        // 1) Sign in Firebase with Google credential
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = firebaseAuth.signInWithCredential(credential).await()
        val firebaseCurrentUser = firebaseAuth.currentUser ?: error("Firebase user is null")

        // 2) Get Firebase token (await!)
        val tokenResult = firebaseCurrentUser
            /* forceRefresh = false*/
            .getIdToken(false)
            .await()

        // 3) Map to your user model
        user.id = firebaseCurrentUser.uid
        UserManager.userID = firebaseCurrentUser.uid

        user.firebaseToken = tokenResult.token.orEmpty()

        tokenResult.expirationTimestamp.let { ms ->
            user.firebaseTokenExpiration = Timestamp(Date(ms))
        }

        // `signInProvider` comes from the token or providerData; token may be null
        user.signInProvider = tokenResult.signInProvider
            ?: firebaseCurrentUser.providerData.firstOrNull()?.providerId
                    ?: "unknown"

        // 4) New vs existing user
        if (authResult.additionalUserInfo?.isNewUser == true) {
            pushUserInfo(user)          // your write-to-FireStore
            UserManager.user = user
            _liveUser.postValue(user)
            _statusLogIn.postValue(FIREBASE_LOG_IN_FIRST)
        } else {
            getUserByID(user.id)        // your read-from-FireStore
            _statusLogIn.postValue(FIREBASE_LOG_IN_EVER)
        }

        _navigateToLoginSuccess.postValue(user)
        leave()

        user
    }

    private fun pushUserInfo(user: User) {
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING
            when (val result = repository.pushUserInfo(user)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                }

                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                }

                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                }

                else -> {
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    private fun getUserByID(id: String) {
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING
            val result = repository.getUserById(id)
            UserManager.user = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    result.data
                }

                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }

                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }

                else -> {
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
            _liveUser.value = UserManager.user
        }
    }
}