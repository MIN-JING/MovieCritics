package com.jim.moviecritics.data

/**
 * A generic class that holds a value with its api status.
 */

sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Fail(val error: String) : Result<Nothing>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[result=$data]"
            is Fail -> "Fail[error=$error]"
            is Error -> "Error[exception=${exception.message}]"
            Loading -> "Loading"
        }
    }
}
