package org.sirekanyan.outline.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import org.sirekanyan.outline.db.model.ServerEntity

class ServerDao(private val database: OutlineDatabase) {

    private val queries = database.serverEntityQueries
    private val keyQueries = database.keyEntityQueries

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
        database.transaction {
            keyQueries.deleteKeys(id)
            queries.delete(id)
        }
    }

}