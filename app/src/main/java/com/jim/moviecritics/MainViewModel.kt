package com.jim.moviecritics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.User
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.login.UserManager
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.CurrentFragmentType
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util.getString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel(private val applicationRepository: ApplicationRepository) : ViewModel() {

    // user: MainViewModel has User info to provide Drawer UI
    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    // Record current fragment to support data binding
    val currentFragmentType = MutableLiveData<CurrentFragmentType>()

    // Handle navigation to login success
    private val _navigateToLoginSuccess = MutableLiveData<User?>()

    val navigateToLoginSuccess: LiveData<User?>
        get() = _navigateToLoginSuccess

    // Handle navigation to profile by bottom nav directly which includes icon change
    private val _navigateToProfileByBottomNav = MutableLiveData<User?>()

    val navigateToProfileByBottomNav: LiveData<User?>
        get() = _navigateToProfileByBottomNav

    // check user login status
    val isLoggedIn
        get() = UserManager.isLoggedIn

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

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]$this")
        Logger.i("------------------------------------")
    }

    fun setupUser(user: User) {

        _user.value = user
        Logger.i("=============")
        Logger.i("| setupUser |")
        Logger.i("user=$user")
        Logger.i("MainViewModel=$this")
        Logger.i("=============")
    }

    fun checkUser() {
        if (user.value == null) {
            Logger.i("MainViewModel UserManager.userToken = ${UserManager.userToken}")
            UserManager.userToken?.let {
                getUser(it)
            }
        }
    }

    fun navigateToLoginSuccess(user: User) {
        _navigateToLoginSuccess.value = user
    }

    fun onLoginSuccessNavigated() {
        _navigateToLoginSuccess.value = null
    }

    fun navigateToProfileByBottomNav(user: User) {
        _navigateToProfileByBottomNav.value = user
    }

    fun onProfileNavigated() {
        _navigateToProfileByBottomNav.value = null
    }

    private fun getUser(token: String) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = applicationRepository.getUserByToken(token)

            _user.value = when (result) {

                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    if (result.error.contains("Invalid Access Token", true)) {
                        UserManager.clear()
                    }
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
        }
    }
}
