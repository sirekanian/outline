package org.sirekanyan.outline.db

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.sirekanyan.outline.app

@Composable
fun rememberKeyValueDao(): KeyValueDao {
    val database = LocalContext.current.app().database
    return remember { KeyValueDao(database) }
}

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