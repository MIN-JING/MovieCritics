package com.jim.moviecritics.network

import com.jim.moviecritics.BuildConfig
import com.jim.moviecritics.data.MovieDetailResult
import com.jim.moviecritics.data.PopularMoviesResult
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
private const val API_KEY = ""
private const val MEDIA_TYPE = "movie"
private const val TIME_WINDOW = "week"

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
            level = when (BuildConfig.LOGGER_VISIABLE) {
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
 * A public interface that exposes the [getPopularMovies] methods
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
        @Query("api_key") apiKey: String = API_KEY
    ): MovieDetailResult
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object TmdbApi {
    val retrofitService: TmdbApiService by lazy { retrofit.create(TmdbApiService::class.java) }
}