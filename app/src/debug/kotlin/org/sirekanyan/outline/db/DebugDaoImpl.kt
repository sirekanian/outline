package org.sirekanyan.outline.db

import org.sirekanyan.outline.BuildConfig
import org.sirekanyan.outline.db.model.ServerEntity
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
            BuildConfig.DEBUG_SERVERS.forEachIndexed { index, url ->
                serverQueries.insert(ServerEntity(url, true, "Server ${index + 1}", null, null))
            }
        }
    }

}