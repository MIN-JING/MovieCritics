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
import com.jim.moviecritics.data.Comment
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.User
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class LoginViewModel(private val applicationRepository: ApplicationRepository)  : ViewModel() {

    private lateinit var googleSignInAccount: GoogleSignInAccount

    private lateinit var firebaseAuth: FirebaseAuth


    val user = User()

    val liveUser = MutableLiveData<User>()

//    val user: LiveData<User>
//        get() = _user

//    val user = MutableLiveData<User>()

    // Handle navigation to login success
    private val _navigateToLoginSuccess = MutableLiveData<User>()

    val navigateToLoginSuccess: LiveData<User>
        get() = _navigateToLoginSuccess

//    // Handle leave login
//    private val _loginGoogle = MutableLiveData<Boolean?>()
//
//    val loginGoogle: LiveData<Boolean?>
//        get() = _loginGoogle

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



//    private fun loginGoogle() {
//        _loginGoogle.value = true
//    }

    fun leave() {
        _leave.value = true
    }

    fun onLeaveCompleted() {
        _leave.value = null
    }


//    fun onLoginGoogleCompleted() {
//        _loginGoogle.value = null
//    }

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            googleSignInAccount = completedTask.getResult(ApiException::class.java)
            val googleId = googleSignInAccount.id ?: ""
            Logger.i("Google ID = $googleId")
            val googleFirstName = googleSignInAccount.givenName ?: ""
            Logger.i("Google First Name = $googleFirstName")
            val googleLastName = googleSignInAccount.familyName ?: ""
            Logger.i("Google Last Name = $googleLastName")
            val googleEmail = googleSignInAccount.email ?: ""
            Logger.i("Google Email = $googleEmail")
            val googleProfilePicURL = googleSignInAccount.photoUrl.toString()
            Logger.i("Google Profile Pic URL = $googleProfilePicURL")
            val googleIdToken = googleSignInAccount.idToken ?: ""
            Logger.i("Google ID Token = $googleIdToken")
            val googleIsExpired = googleSignInAccount.isExpired
            Logger.i("Google isExpired = $googleIsExpired")

            googleSignInAccount.idToken?.let { firebaseAuthWithGoogle(it) }

            user.name = googleSignInAccount.givenName + "  " + googleSignInAccount.familyName
            Logger.i("user.name = ${user.name}")
            user.email = googleSignInAccount.email.toString()
            Logger.i("user.email = ${user.email}")
            user.pictureUri = googleSignInAccount.photoUrl.toString()
            Logger.i("user.pictureUri = ${user.pictureUri}")

        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Logger.e("Google log in failed code = ${e.statusCode}")
        }

//        _user.value?.name = googleSignInAccount.givenName + "" + googleSignInAccount.familyName
//        Logger.i("_user.value?.name = ${_user.value?.name}")
//        _user.value?.email = googleSignInAccount.email.toString()
//        Logger.i("_user.value?.email = ${_user.value?.email}")
//        _user.value?.pictureUri = googleSignInAccount.photoUrl.toString()
//        Logger.i("_user.value?.pictureUri = ${_user.value?.pictureUri}")

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        firebaseAuth = Firebase.auth

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("signInWithCredential:success")

                    val firebaseCurrentUser = firebaseAuth.currentUser
                    Logger.i("signInWithCredential user.providerId = ${firebaseCurrentUser?.providerId}")
                    Logger.i("signInWithCredential user.uid = ${firebaseCurrentUser?.uid}")

                    user.id = firebaseCurrentUser?.uid.toString()
                    Logger.i("user.id = ${user.id}")

                    val firebaseTokenResult = firebaseCurrentUser?.getIdToken(false)?.result
                    Logger.i("signInWithCredential user.getIdToken.result.token = ${firebaseTokenResult?.token}")
                    Logger.i("signInWithCredential user.getIdToken.result.expirationTimestamp = ${firebaseTokenResult?.expirationTimestamp}")
                    Logger.i("signInWithCredential user.getIdToken.result.signInProvider = ${firebaseTokenResult?.signInProvider}")

                    user.firebaseToken = firebaseTokenResult?.token.toString()
                    Logger.i("user.firebaseToken = ${user.firebaseToken}")

                    val firebaseDate = firebaseTokenResult?.expirationTimestamp?.let { Date(it) }

                    if (firebaseDate != null) {
                        user.firebaseTokenExpiration = Timestamp(firebaseDate)
                        Logger.i("user.firebaseTokenExpiration = ${user.firebaseTokenExpiration}")
                    }

                    user.signInProvider = firebaseTokenResult?.signInProvider.toString()
                    Logger.i("user.signInProvider = ${user.signInProvider}")

                    UserManager.userToken = firebaseTokenResult?.token.toString()

//                    _user.value = _user.value
                    liveUser.value = user

                } else {
                    Logger.w("signInWithCredential:failure e = ${task.exception}")
                }
            }
    }

    fun userSignIn(user: User) {
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = applicationRepository.userSignIn(user)) {
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

}