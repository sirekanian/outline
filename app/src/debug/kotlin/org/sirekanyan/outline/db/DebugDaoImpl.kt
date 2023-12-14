package org.sirekanyan.outline.db

import org.sirekanyan.outline.api.model.createServerEntity
import org.sirekanyan.outline.isDebugBuild

class DebugDaoImpl(private val database: OutlineDatabase) : DebugDao {

    private val keyQueries = database.keyEntityQueries
    private val serverQueries = database.serverEntityQueries

    init {
        require(isDebugBuild()) {
            error("Not allowed in production builds")
        }
    }

    override fun reset() {
        database.transaction {
            keyQueries.truncate()
            serverQueries.truncate()
            listOf<String>(
                // add your debug servers here
            ).forEach { url ->
                serverQueries.insert(createServerEntity(url, insecure = true))
            }
        }
    }

}