package com.jim.moviecritics.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jim.moviecritics.MovieApplication
import com.jim.moviecritics.R
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.User
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel(private val applicationRepository: ApplicationRepository) : ViewModel() {

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
                if (task.isSuccessful) {
                    Logger.i("signInWithCredential:success")

                    val firebaseCurrentUser = firebaseAuth.currentUser
                    val firebaseTokenResult = firebaseCurrentUser?.getIdToken(false)?.result

                    user.id = firebaseCurrentUser?.uid.toString()
                    UserManager.userId = firebaseCurrentUser?.uid.toString()
                    Logger.i("UserManager.userId = ${UserManager.userId}")
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
                        Logger.i("signInWithCredential user.uid = ${firebaseCurrentUser?.uid}")
                        Logger.i("isNewUser == true, user = $user")
                        pushUserInfo(user)
                        UserManager.user = user
                        _liveUser.value = user
                        _statusLogIn.value = FIREBASE_LOG_IN_FIRST
                        leave()
                    } else {
                        Logger.i("Firebase additionalUserInfo.isNewUser == false")
                        Logger.i("isNewUser == false, user = $user")
                        getUserById(user.id)
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

            when (val result = applicationRepository.pushUserInfo(user)) {
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
                    _error.value = MovieApplication.instance.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    private fun getUserById(id: String) {
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            val result = applicationRepository.getUserById(id)
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
                    _error.value = MovieApplication.instance.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
            _liveUser.value = UserManager.user
        }
    }

    companion object {

        const val FIREBASE_LOG_IN_FIRST = 0x11
        const val FIREBASE_LOG_IN_EVER = 0x12

        const val NO_ONE_KNOWS = 0x21
    }
}
