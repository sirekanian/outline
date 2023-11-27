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
import org.sirekanyan.outline.db.model.ServerEntity

@Composable
fun rememberServerDao(): ServerDao {
    val database = LocalContext.current.app().database
    return remember { ServerDao(database) }
}

class ServerDao(database: OutlineDatabase) {

    private val queries = database.serverEntityQueries

    fun observeUrls(): Flow<List<ServerEntity>> =
        queries.selectUrls().asFlow().mapToList(Dispatchers.IO)

    suspend fun insertUrl(server: ServerEntity) =
        withContext(Dispatchers.IO) {
            queries.insertUrl(server)
        }

    suspend fun deleteUrl(id: String) {
        withContext(Dispatchers.IO) {
            queries.deleteUrl(id)
        }
    }

}