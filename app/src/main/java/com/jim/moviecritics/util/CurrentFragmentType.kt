package com.jim.moviecritics.util

import com.jim.moviecritics.R
import com.jim.moviecritics.util.Util.getString

enum class CurrentFragmentType(val value: String) {
    HOME(getString(R.string.movie_critics)),
    SEARCH(getString(R.string.search)),
    WATCHLIST(getString(R.string.watchlist)),
    PROFILE(getString(R.string.profile)),
    DETAIL(getString(R.string.detail))
}