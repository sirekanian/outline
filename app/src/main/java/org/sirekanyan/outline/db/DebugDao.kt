package org.sirekanyan.outline.db

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.sirekanyan.outline.BuildConfig
import org.sirekanyan.outline.db.model.ServerEntity

class DebugDao(private val database: OutlineDatabase) {

    private val serverQueries = database.serverEntityQueries

    init {
        require(BuildConfig.DEBUG) {
            error("Not allowed in production builds")
        }
    }

    suspend fun reset() {
        withContext(Dispatchers.IO) {
            database.transaction {
                serverQueries.truncate()
                listOf<String>(
                    // add your debug servers here
                ).forEach { url ->
                    serverQueries.insertUrl(ServerEntity(url, insecure = true))
                }
            }
        }
    }

}