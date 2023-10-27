package org.sirekanyan.outline

import android.app.Application
import android.content.Context

@Suppress("KotlinConstantConditions")
fun isPlayFlavor(): Boolean =
    BuildConfig.FLAVOR == "play"

class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        CrashReporter.init(this)
    }

}