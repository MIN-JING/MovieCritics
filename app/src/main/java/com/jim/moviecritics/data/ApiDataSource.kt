package com.jim.moviecritics.data

interface ApiDataSource {

    suspend fun getPopularMovie(): Result<List<HomeItem>>
}