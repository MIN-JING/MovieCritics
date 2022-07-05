package com.jim.moviecritics.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.jim.moviecritics.MovieApplication
import com.jim.moviecritics.data.User


object UserManager {

    private const val USER_DATA = "user_data"
    private const val USER_TOKEN = "user_token"
    private const val USER_ID = "user_id"

//    private val _user = MutableLiveData<User?>()
//
//    val user: LiveData<User?>
//        get() = _user

    val user = MutableLiveData<User?>()

    var userToken: String? = null
        get() = MovieApplication.instance
            .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)
            .getString(USER_TOKEN, null)
        set(value) {
            field = when (value) {
                null -> {
                    MovieApplication.instance
                        .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit()
                        .remove(USER_TOKEN)
                        .apply()
                    null
                }
                else -> {
                    MovieApplication.instance
                        .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit()
                        .putString(USER_TOKEN, value)
                        .apply()
                    value
                }
            }
        }

    var userId: String? = null
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
        get() = userToken != null

    /**
     * Clear the [userToken] and the [user]/[_user] data
     */
    fun clear() {
        userToken = null
//        _user.value = null
        user.value = null
    }
}