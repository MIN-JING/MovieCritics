package com.jim.moviecritics.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideosResult(
    val results: List<Video>?
) : Parcelable