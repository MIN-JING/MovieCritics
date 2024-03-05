package com.jim.moviecritics.network

import com.jim.moviecritics.BuildConfig
import com.jim.moviecritics.data.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val HOST_NAME = "api.themoviedb.org"
private const val API_VERSION = "3"
private const val BASE_URL = "https://$HOST_NAME/$API_VERSION/"
private const val API_KEY = BuildConfig.API_KEY_TMDB
private const val MEDIA_TYPE = "movie"
private const val TIME_WINDOW = "week"
private const val EXTERNAL_SOURCE = "imdb_id"
private const val APPEND_TO_RESPONSE = "videos"

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

private val client = OkHttpClient.Builder()
    .addInterceptor(
        HttpLoggingInterceptor().apply {
            level = when (BuildConfig.LOGGER_VISIBLE) {
                true -> HttpLoggingInterceptor.Level.BODY
                false -> HttpLoggingInterceptor.Level.NONE
            }
        }
    )
    .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(client)
    .build()

/**
 * A public interface that exposes the methods
 */
interface TmdbApiService {

    @GET("trending/{media_type}/{time_window}")
    suspend fun getPopularMovies(
        @Path("media_type") type: String = MEDIA_TYPE,
        @Path("time_window") timeWindow: String = TIME_WINDOW,
        @Query("api_key") apiKey: String = API_KEY
    ): PopularMoviesResult

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("append_to_response") append: String = APPEND_TO_RESPONSE
    ): MovieDetailResult

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredit(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String = API_KEY
    ): CreditResult

    @GET("search/multi")
    suspend fun getSearchMulti(
        @Query("query") queryKey: String,
        @Query("api_key") apiKey: String = API_KEY
    ): SearchResult

    @GET("find/{external_id}")
    suspend fun getFind(
        @Path("external_id") id: String,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("external_source") externalSource: String = EXTERNAL_SOURCE
    ): FindResult
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object TmdbApi {
    val retrofitService: TmdbApiService by lazy { retrofit.create(TmdbApiService::class.java) }
}
