package org.sirekanyan.outline.db

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import org.sirekanyan.outline.app
import org.sirekanyan.outline.db.model.ServerEntity

@Composable
fun rememberServerDao(): ServerDao {
    val database = LocalContext.current.app().database
    return remember { ServerDao(database) }
}

class ServerDao(database: OutlineDatabase) {

    private val queries = database.serverEntityQueries

    fun selectAll(): List<ServerEntity> =
        queries.selectAll().executeAsList()

    fun observeAll(): Flow<Query<ServerEntity>> =
        queries.selectAll().asFlow()

    fun insert(server: ServerEntity) {
        queries.insert(server)
    }

    fun insertAll(servers: List<ServerEntity>) {
        queries.transaction {
            servers.forEach {
                queries.insert(it)
            }
        }
    }

    fun deleteUrl(id: String) {
        queries.delete(id)
    }

}