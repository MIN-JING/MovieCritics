package com.jim.moviecritics.watchlist

import androidx.lifecycle.ViewModel
import com.jim.moviecritics.data.User
import com.jim.moviecritics.data.source.ApplicationRepository

class WatchlistViewModel(
    private val applicationRepository: ApplicationRepository,
    private val arguments: User?
    ) : ViewModel() {
    // TODO: Implement the ViewModel
}