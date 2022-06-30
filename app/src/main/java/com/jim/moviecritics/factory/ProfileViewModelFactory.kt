package com.jim.moviecritics.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jim.moviecritics.data.User
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.profile.ProfileViewModel


@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory(
    private val applicationRepository: ApplicationRepository,
    private val user: User?
)   : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>)=
        with(modelClass) {
        when {
            isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(applicationRepository, user)

            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}