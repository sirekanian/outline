package org.sirekanyan.outline.db

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import org.sirekanyan.outline.api.model.Server
import org.sirekanyan.outline.app
import org.sirekanyan.outline.db.model.KeyEntity
import org.sirekanyan.outline.db.model.KeyWithServerEntity

@Composable
fun rememberKeyDao(): KeyDao {
    val database = LocalContext.current.app().database
    return remember { KeyDao(database) }
}

class KeyDao(database: OutlineDatabase) {

    private val queries = database.keyEntityQueries

    fun observe(server: Server): Flow<Query<KeyEntity>> =
        queries.selectKeys(server.id).asFlow()

    fun observeAll(query: String): Flow<Query<KeyWithServerEntity>> =
        queries.selectAllKeys("%$query%").asFlow()

    fun update(server: Server, keys: List<KeyEntity>) {
        queries.transaction {
            queries.deleteKeys(server.id)
            keys.forEach(queries::insertKey)
        }
    }

}