package com.dante2194.okalastores

import android.app.Application
import com.dante2194.okalastores.data.AppContainer

class OkalaApp : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
