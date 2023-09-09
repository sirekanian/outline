package org.sirekanyan.outline

import android.app.Application
import android.content.Context

class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        CrashReporter.init(this)
    }

}