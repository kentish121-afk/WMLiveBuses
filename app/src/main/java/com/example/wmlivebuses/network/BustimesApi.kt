package com.example.wmlivebuses.network

import com.example.wmlivebuses.model.OperatorListResponse
import com.example.wmlivebuses.model.VehicleListResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface BustimesApi {

    @GET("api/vehicles/")
    suspend fun searchVehicles(
        @Query("search") search: String? = null,
        @Query("operator") operator: String? = null,
        @Query("withdrawn") withdrawn: Boolean? = false,
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0
    ): VehicleListResponse

    @GET("api/operators/")
    suspend fun getOperators(
        @Query("search") search: String? = null,
        @Query("limit") limit: Int = 50
    ): OperatorListResponse

    companion object {
        private const val BASE_URL = "https://bustimes.org/"

        fun create(): BustimesApi {
            val json = Json {
                ignoreUnknownKeys = true
                isLenient = true
                coerceInputValues = true
            }

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header("User-Agent", "WMLiveBuses/1.0 (Android; educational; uses public bustimes.org data)")
                        .header("Accept", "application/json")
                        .build()
                    chain.proceed(request)
                }
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(BustimesApi::class.java)
        }
    }
}
