package org.sirekanyan.outline

import android.app.Application
import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.sirekanyan.outline.db.OutlineDatabase

fun Context.app(): App =
    applicationContext as App

fun isDebugBuild(): Boolean =
    BuildConfig.DEBUG

@Suppress("KotlinConstantConditions")
fun isPlayFlavor(): Boolean =
    BuildConfig.FLAVOR == "play"

class App : Application() {

    val database: OutlineDatabase by lazy {
        OutlineDatabase(AndroidSqliteDriver(OutlineDatabase.Schema, this, "outline.db"))
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        CrashReporter.init(this)
    }

}