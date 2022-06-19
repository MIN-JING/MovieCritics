package com.jim.moviecritics.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Actor(
    val id: Long,
    val creditID: String,
    val name: String
) : Parcelable
