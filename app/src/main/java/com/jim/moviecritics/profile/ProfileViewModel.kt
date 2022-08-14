package com.jim.moviecritics.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.User
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.login.UserManager
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: Repository,
    private val arguments: User?
) : ViewModel() {

    // After login to Firebase server through Google
    // at the same time we can get user info to provide to display ui
    private val _user = MutableLiveData<User>().apply {
        arguments?.let {
            value = it
        }
    }

    val user: LiveData<User>
        get() = _user

    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

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
        if (user.value == null) {
            UserManager.userID?.let { getUserByID(it) }
        }
    }

    private fun getUserByID(userID: String) {
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING
            val result = repository.getUserById(userID)
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
            _user.value = UserManager.user
        }
    }
}
