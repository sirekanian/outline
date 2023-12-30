package org.sirekanyan.outline

import android.app.Application
import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.sirekanyan.outline.api.OutlineApi
import org.sirekanyan.outline.db.DebugDao
import org.sirekanyan.outline.db.DebugDaoImpl
import org.sirekanyan.outline.db.KeyDao
import org.sirekanyan.outline.db.KeyValueDao
import org.sirekanyan.outline.db.OutlineDatabase
import org.sirekanyan.outline.db.ServerDao
import org.sirekanyan.outline.repository.KeyRepository
import org.sirekanyan.outline.repository.ServerRepository

fun Context.app(): App =
    applicationContext as App

fun isDebugBuild(): Boolean =
    BuildConfig.DEBUG

@Suppress("KotlinConstantConditions")
fun isPlayFlavor(): Boolean =
    BuildConfig.FLAVOR == "play"

class App : Application() {

    private val api by lazy { OutlineApi() }
    private val database by lazy {
        OutlineDatabase(AndroidSqliteDriver(OutlineDatabase.Schema, this, "outline.db"))
    }
    private val serverDao = ServerDao(database)
    val serverRepository by lazy { ServerRepository(api, serverDao) }
    val keyRepository by lazy { KeyRepository(api, KeyDao(database), serverDao) }
    val prefsDao by lazy { KeyValueDao(database) }
    val debugDao: DebugDao by lazy { DebugDaoImpl(database) }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        CrashReporter.init(this)
    }

}