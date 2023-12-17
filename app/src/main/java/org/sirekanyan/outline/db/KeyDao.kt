package org.sirekanyan.outline.db

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import org.sirekanyan.outline.app
import org.sirekanyan.outline.db.model.KeyEntity
import org.sirekanyan.outline.db.model.KeyWithServerEntity
import org.sirekanyan.outline.db.model.ServerEntity

@Composable
fun rememberKeyDao(): KeyDao {
    val database = LocalContext.current.app().database
    return remember { KeyDao(database) }
}

class KeyDao(database: OutlineDatabase) {

    private val queries = database.keyEntityQueries

    fun observe(server: ServerEntity): Flow<Query<KeyEntity>> =
        queries.selectKeys(server.id).asFlow()

    fun observeAll(): Flow<Query<KeyWithServerEntity>> =
        queries.selectAllKeys().asFlow()

    fun update(server: ServerEntity, keys: List<KeyEntity>) {
        queries.transaction {
            queries.deleteKeys(server.id)
            keys.forEach(queries::insertKey)
        }
    }

}