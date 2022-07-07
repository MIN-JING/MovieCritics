package com.jim.moviecritics.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jim.moviecritics.data.Comment
import com.jim.moviecritics.data.User
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.login.UserManager
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class ItemGuideViewModel(
    private val applicationRepository: ApplicationRepository
) : ViewModel() {

    private val user = UserManager.user

//    private val _user = MutableLiveData<User>()
//
//    val user: LiveData<User>
//        get() = _user


    var livePersonalComments = MutableLiveData<List<Comment>>()

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
     * service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Get [User] profile data when user is null
     */
    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]$this")
        Logger.i("------------------------------------")

        user?.id?.let { getLivePersonalCommentsResult(it) }

//        if (livePersonalComments.value == null) {
//            Logger.i("livePersonalComments.value == null")
//            UserManager.userId?.let {
//                getLivePersonalCommentsResult(it)
//            }
//        } else {
//            Logger.i("livePersonalComments.value != null")
//        }
    }

    private fun getLivePersonalCommentsResult(userID: String) {
        livePersonalComments = applicationRepository.getLivePersonalComments(userID)
        _status.value = LoadApiStatus.DONE
    }
}