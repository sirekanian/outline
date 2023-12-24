package org.sirekanyan.outline.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class KeyValueDao(database: OutlineDatabase) {

    private val queries = database.keyValueEntityQueries

    fun observe(key: String): Flow<String?> =
        queries.select(key).asFlow().mapToOneNotNull(Dispatchers.IO).map { it.content }

    suspend fun find(key: String): String? =
        withContext(Dispatchers.IO) {
            queries.select(key).executeAsOneOrNull()?.content
        }

    suspend fun put(key: String, value: String) {
        withContext(Dispatchers.IO) {
            queries.insert(key, value)
        }
    }

    suspend fun delete(key: String) {
        withContext(Dispatchers.IO) {
            queries.delete(key)
        }
    }

}