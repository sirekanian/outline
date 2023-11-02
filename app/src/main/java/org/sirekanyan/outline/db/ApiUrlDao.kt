package org.sirekanyan.outline.db

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.sirekanyan.outline.app
import org.sirekanyan.outline.db.model.ApiUrl

@Composable
fun rememberApiUrlDao(): ApiUrlDao {
    val database = LocalContext.current.app().database
    return remember { ApiUrlDao(database) }
}

class ApiUrlDao(database: OutlineDatabase) {

    private val queries = database.apiUrlQueries

    fun observeUrls(): Flow<List<ApiUrl>> =
        queries.selectUrls().asFlow().mapToList(Dispatchers.IO)

    suspend fun insertUrl(url: ApiUrl) =
        withContext(Dispatchers.IO) {
            queries.insertUrl(url)
        }

    suspend fun deleteUrl(id: String) {
        withContext(Dispatchers.IO) {
            queries.deleteUrl(id)
        }
    }

}