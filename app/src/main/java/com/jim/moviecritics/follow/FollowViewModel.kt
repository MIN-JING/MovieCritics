package com.jim.moviecritics.follow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jim.moviecritics.data.User
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.util.Logger


class FollowViewModel(
    private val repository: Repository,
    private val arguments: User?
) : ViewModel() {

    private val _user = MutableLiveData<User>().apply {
        value = arguments
    }

    val user: LiveData<User>
        get() = _user

    private val _navigateToBlock = MutableLiveData<User?>()

    val navigateToBlock: LiveData<User?>
        get() = _navigateToBlock

    // Handle leave login
    private val _leave = MutableLiveData<Boolean?>()

    val leave: LiveData<Boolean?>
        get() = _leave

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

    fun navigateToBlock(user: User) {
        _navigateToBlock.value = user
    }

    fun onBlockNavigated() {
        _navigateToBlock.value = null
    }
}
