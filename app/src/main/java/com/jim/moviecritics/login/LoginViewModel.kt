package com.jim.moviecritics.login

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.User
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.jim.moviecritics.R
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption

class LoginViewModel(private val repository: Repository) : ViewModel() {

    companion object {
        const val FIREBASE_LOG_IN_FIRST = 0x11
        const val FIREBASE_LOG_IN_EVER = 0x12
        const val NO_ONE_KNOWS = 0x21
    }

    private lateinit var googleSignInAccount: GoogleSignInAccount
    private lateinit var firebaseAuth: FirebaseAuth

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

    fun signInWithGoogle2(activity: Activity) {
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

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            try {
                // 1) Get Google ID token via Credential Manager
                val clientId = context.getString(R.string.default_web_client_id)
                Logger.i("Google clientId = $clientId")
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(clientId)
                    .setFilterByAuthorizedAccounts(false)
                    .build()

                val request = GetCredentialRequest(listOf(googleIdOption))
                val credentialManager = CredentialManager.create(context)
                val result = credentialManager.getCredential(context, request)

                val credential = GoogleIdTokenCredential.createFrom(result.credential.data)
                val idToken = credential.idToken

                // 2) Call the suspend auth method
                firebaseAuthWithGoogleSuspend(idToken, user)
                    .onFailure { e ->
                        Logger.w("signInWithCredential:failure e = $e")
                        _statusLogIn.postValue(NO_ONE_KNOWS)
                    }

            } catch (e: androidx.credentials.exceptions.GetCredentialException) {
                when (e) {
                    is androidx.credentials.exceptions.NoCredentialException -> {
                        // No eligible creds found (no Google account / no Play services / bad client id)
                        Logger.w("NoCredentialException: ${e.message}")
                        // Optional: surface a user hint
                        // showMessage("No Google account found. Add an account or update Google Play services.")
                    }

                    is androidx.credentials.exceptions.GetCredentialCancellationException -> {
                        Logger.w("User cancelled: ${e.message}")
                    }

                    is androidx.credentials.exceptions.GetCredentialProviderConfigurationException -> {
                        Logger.w("Provider misconfigured (check Play services / web client id / SHA keys): ${e.message}")
                    }

                    else -> {
                        Logger.w("GetCredentialException: ${e::class.simpleName}: ${e.message}")
                    }
                }
                _statusLogIn.postValue(NO_ONE_KNOWS)
            } catch (ce: CancellationException) {
                // coroutine cancelled (navigate away, etc.)
                Logger.w("Google sign-in cancelled: ${ce.message}")
                _statusLogIn.postValue(NO_ONE_KNOWS)
            } catch (t: Throwable) {
                Logger.w("Google sign-in failed: ${t.message}")
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
        val tokenResult = firebaseCurrentUser.getIdToken(/* forceRefresh = */ false).await()

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

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            googleSignInAccount = completedTask.getResult(ApiException::class.java)
            val googleId = googleSignInAccount.id ?: ""
            Logger.i("Google ID = $googleId")
            googleSignInAccount.idToken?.let { firebaseAuthWithGoogle(it) }
            user.name = googleSignInAccount.givenName + "  " + googleSignInAccount.familyName
            user.email = googleSignInAccount.email.toString()
            user.pictureUri = googleSignInAccount.photoUrl.toString()
        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Logger.e("Google log in failed code = ${e.statusCode}")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth = Firebase.auth

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                Logger.d("signInWithCredential: $task")
                if (task.isSuccessful) {
                    Logger.i("signInWithCredential:success")

                    val firebaseCurrentUser = firebaseAuth.currentUser
                    val firebaseTokenResult = firebaseCurrentUser?.getIdToken(false)?.result

                    user.id = firebaseCurrentUser?.uid.toString()
                    UserManager.userID = firebaseCurrentUser?.uid.toString()
                    Logger.i("UserManager.userID = ${UserManager.userID}")
                    user.firebaseToken = firebaseTokenResult?.token.toString()
                    Logger.i("Firebase Token = ${firebaseTokenResult?.token}")

                    val firebaseDate = firebaseTokenResult?.expirationTimestamp?.let { Date(it) }

                    if (firebaseDate != null) {
                        user.firebaseTokenExpiration = Timestamp(firebaseDate)
                    }

                    user.signInProvider = firebaseTokenResult?.signInProvider.toString()

                    _navigateToLoginSuccess.value = user

                    if (task.result.additionalUserInfo?.isNewUser == true) {
                        Logger.i("Firebase additionalUserInfo.isNewUser == true")
                        Logger.i(
                            "signInWithCredential user.uid" +
                                    "= ${firebaseCurrentUser?.uid}"
                        )
                        Logger.i("isNewUser == true, user = $user")
                        pushUserInfo(user)
                        UserManager.user = user
                        _liveUser.value = user
                        _statusLogIn.value = FIREBASE_LOG_IN_FIRST
                        leave()
                    } else {
                        Logger.i("Firebase additionalUserInfo.isNewUser == false")
                        Logger.i("isNewUser == false, user = $user")
                        getUserByID(user.id)
                        _statusLogIn.value = FIREBASE_LOG_IN_EVER
                        leave()
                    }
                } else {
                    Logger.w("signInWithCredential:failure e = ${task.exception}")
                    _statusLogIn.value = NO_ONE_KNOWS
                }
            }
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