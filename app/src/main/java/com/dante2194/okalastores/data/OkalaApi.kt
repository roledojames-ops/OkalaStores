package com.dante2194.okalastores.data

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface OkalaApi {

    @GET("opex/v4/stores/nearby")
    suspend fun getNearbyStores(
        @Query("Latitude") latitude: Double,
        @Query("Longitude") longitude: Double,
    ): NearbyResponse

    @GET("opex/v2/stores/details")
    suspend fun getStoreDetails(
        @Query("Latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("StoreId") storeId: Int,
        @Query("customerSegments") customerSegments: String = "None",
    ): DetailResponse
}

object ApiFactory {

    val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        explicitNulls = false
    }

    fun create(): OkalaApi {
        val client = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .callTimeout(45, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Accept", "application/json")
                    .header("User-Agent", "OkalaStores/1.0 (Android)")
                    .header("Origin", "https://www.okala.com")
                    .header("Referer", "https://www.okala.com/stores")
                    .build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://apigateway.okala.com/api/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(OkalaApi::class.java)
    }
}
