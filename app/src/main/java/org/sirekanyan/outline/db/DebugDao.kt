package org.sirekanyan.outline.db

import org.sirekanyan.outline.db.model.ServerEntity
import org.sirekanyan.outline.isDebugBuild

class DebugDao(private val database: OutlineDatabase) {

    private val keyQueries = database.keyEntityQueries
    private val serverQueries = database.serverEntityQueries

    init {
        require(isDebugBuild()) {
            error("Not allowed in production builds")
        }
    }

    fun reset() {
        database.transaction {
            keyQueries.truncate()
            serverQueries.truncate()
            listOf<String>(
                // add your debug servers here
            ).forEach { url ->
                serverQueries.insertUrl(ServerEntity(url, insecure = true))
            }
        }
    }

}