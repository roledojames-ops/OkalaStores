package com.dante2194.okalastores.data

import android.content.Context

class StoreCache(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("okala_stores_cache", Context.MODE_PRIVATE)

    fun saveNearby(data: NearbyData) {
        runCatching {
            prefs.edit()
                .putString(KEY_NEARBY, ApiFactory.json.encodeToString(NearbyData.serializer(), data))
                .putLong(KEY_SAVED_AT, System.currentTimeMillis())
                .apply()
        }
    }

    fun loadNearby(): NearbyData? = runCatching {
        prefs.getString(KEY_NEARBY, null)?.let {
            ApiFactory.json.decodeFromString(NearbyData.serializer(), it)
        }
    }.getOrNull()

    fun savedAt(): Long = prefs.getLong(KEY_SAVED_AT, 0L)

    private companion object {
        const val KEY_NEARBY = "nearby"
        const val KEY_SAVED_AT = "saved_at"
    }
}
