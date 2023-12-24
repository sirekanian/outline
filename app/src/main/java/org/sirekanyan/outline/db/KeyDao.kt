package org.sirekanyan.outline.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import org.sirekanyan.outline.api.model.Server
import org.sirekanyan.outline.db.model.KeyEntity
import org.sirekanyan.outline.db.model.KeyWithServerEntity

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