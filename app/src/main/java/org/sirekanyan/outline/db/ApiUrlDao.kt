package org.sirekanyan.outline.db

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow

@Composable
fun rememberApiUrlDao(): ApiUrlDao {
    val app = LocalContext.current.applicationContext as Application
    return remember { ApiUrlDao(app) }
}

class ApiUrlDao(app: Application) {

    private val driver = AndroidSqliteDriver(OutlineDatabase.Schema, app, "outline.db")
    private val queries = OutlineDatabase(driver).apiUrlQueries

    fun observeUrls(): Flow<List<String>> =
        queries.selectUrls().asFlow().mapToList()

    fun insertUrl(id: String) {
        queries.insertUrl(id)
    }

    fun deleteUrl(id: String) {
        queries.deleteUrl(id)
    }

}