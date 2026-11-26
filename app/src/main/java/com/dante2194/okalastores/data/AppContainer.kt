package com.dante2194.okalastores.data

import android.content.Context

class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    val api: OkalaApi by lazy { ApiFactory.create() }
    val cache: StoreCache by lazy { StoreCache(appContext) }
    val repository: StoreRepository by lazy { StoreRepository(api, cache) }
    val locationProvider: LocationProvider by lazy { LocationProvider(appContext) }
}
