package org.sirekanyan.outline.db

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.sirekanyan.outline.db.model.ApiUrl

@Composable
fun rememberApiUrlDao(): ApiUrlDao {
    val app = LocalContext.current.applicationContext as Application
    return remember { ApiUrlDao(app) }
}

class ApiUrlDao(app: Application) {

    private val driver = AndroidSqliteDriver(OutlineDatabase.Schema, app, "outline.db")
    private val queries = OutlineDatabase(driver).apiUrlQueries

    fun observeUrls(): Flow<List<ApiUrl>> =
        queries.selectUrls().asFlow().mapToList(Dispatchers.IO)

    fun insertUrl(url: ApiUrl) {
        queries.insertUrl(url)
    }

    fun deleteUrl(id: String) {
        queries.deleteUrl(id)
    }

}