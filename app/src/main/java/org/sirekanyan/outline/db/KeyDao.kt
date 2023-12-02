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
import org.sirekanyan.outline.db.model.KeyEntity
import org.sirekanyan.outline.db.model.ServerEntity

@Composable
fun rememberKeyDao(): KeyDao {
    val database = LocalContext.current.app().database
    return remember { KeyDao(database) }
}

class KeyDao(database: OutlineDatabase) {

    private val queries = database.keyEntityQueries

    fun observe(server: ServerEntity): Flow<List<KeyEntity>> =
        queries.selectKeys(server.id).asFlow().mapToList(Dispatchers.IO)

    suspend fun update(server: ServerEntity, keys: List<KeyEntity>) {
        withContext(Dispatchers.IO) {
            queries.transaction {
                queries.deleteKeys(server.id)
                keys.forEach(queries::insertKey)
            }
        }
    }

}