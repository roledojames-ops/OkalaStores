package com.dante2194.okalastores.data

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

data class NearbyResult(
    val data: NearbyData,
    val fromCache: Boolean,
)

class StoreRepository(
    private val api: OkalaApi,
    private val cache: StoreCache,
) {

    private var lastNearby: NearbyData? = null
    private var storesById: Map<Int, StoreDto> = emptyMap()

    private var lastLatitude: Double = DEFAULT_LATITUDE
    private var lastLongitude: Double = DEFAULT_LONGITUDE

    suspend fun loadNearby(latitude: Double, longitude: Double): NearbyResult =
        withContext(Dispatchers.IO) {
            lastLatitude = latitude
            lastLongitude = longitude
            try {
                val response = api.getNearbyStores(latitude, longitude)
                val data = response.data
                if (response.success && data != null) {
                    lastNearby = data
                    storesById = data.stores.associateBy { it.storeId }
                    cache.saveNearby(data)
                    NearbyResult(data = data, fromCache = false)
                } else {
                    throw IOException(response.message ?: "پاسخ نامعتبر از سرور")
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                val cached = lastNearby ?: cache.loadNearby()
                if (cached != null) {
                    lastNearby = cached
                    storesById = cached.stores.associateBy { it.storeId }
                    NearbyResult(data = cached, fromCache = true)
                } else {
                    throw throwable
                }
            }
        }

    suspend fun loadDetails(storeId: Int): Result<StoreDetailDto> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getStoreDetails(
                    latitude = lastLatitude,
                    longitude = lastLongitude,
                    storeId = storeId,
                )
                Result.success(
                    response.data ?: throw IOException(response.message ?: "جزئیات فروشگاه دریافت نشد"),
                )
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                Result.failure(throwable)
            }
        }

    fun cachedStore(storeId: Int): StoreDto? = storesById[storeId]

    companion object {
        const val DEFAULT_LATITUDE = 35.7000
        const val DEFAULT_LONGITUDE = 51.4000
    }
}
