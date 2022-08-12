package com.jim.moviecritics.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jim.moviecritics.block.BlockViewModel
import com.jim.moviecritics.data.User
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.follow.FollowViewModel
import com.jim.moviecritics.profile.ProfileViewModel
import com.jim.moviecritics.watchlist.WatchlistViewModel

@Suppress("UNCHECKED_CAST")
class UserViewModelFactory(
    private val repository: Repository,
    private val user: User?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(ProfileViewModel::class.java) ->
                    ProfileViewModel(repository, user)

                isAssignableFrom(WatchlistViewModel::class.java) ->
                    WatchlistViewModel(repository, user)

                isAssignableFrom(FollowViewModel::class.java) ->
                    FollowViewModel(repository, user)

                isAssignableFrom(BlockViewModel::class.java) ->
                    BlockViewModel(repository, user)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}