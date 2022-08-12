package com.jim.moviecritics.login

import android.content.Context
import com.jim.moviecritics.MovieApplication
import com.jim.moviecritics.data.User

object UserManager {

    private const val USER_DATA = "user_data"
    private const val USER_ID = "user_id"

    var user: User? = null

    var userID: String? = null
        get() = MovieApplication.instance
            .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)
            .getString(USER_ID, null)
        set(value) {
            field = when (value) {
                null -> {
                    MovieApplication.instance
                        .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit()
                        .remove(USER_ID)
                        .apply()
                    null
                }
                else -> {
                    MovieApplication.instance
                        .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit()
                        .putString(USER_ID, value)
                        .apply()
                    value
                }
            }
        }

    /**
     * It can be use to check login status directly
     */
    val isLoggedIn: Boolean
        get() = userID != null

    /**
     * Clear the [userID]
     */
    fun clear() {
        userID = null
    }
}