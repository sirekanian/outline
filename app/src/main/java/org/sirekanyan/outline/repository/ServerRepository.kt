package org.sirekanyan.outline.repository

import org.sirekanyan.outline.api.OutlineApi
import org.sirekanyan.outline.api.model.getHost
import org.sirekanyan.outline.db.model.ServerEntity
import org.sirekanyan.outline.ext.logDebug
import java.util.concurrent.ConcurrentHashMap

class ServerRepository(private val api: OutlineApi) {

    private val cache: MutableMap<String, ServerEntity> = ConcurrentHashMap()

    fun getCachedServer(server: ServerEntity): ServerEntity =
        cache[server.id] ?: server.copy(name = server.getHost(), traffic = null)

    suspend fun fetchServer(server: ServerEntity): ServerEntity =
        api.getServer(server).also { fetched ->
            cache[server.id] = fetched
        }

    suspend fun getServer(server: ServerEntity): ServerEntity {
        if (!cache.containsKey(server.id)) {
            try {
                return fetchServer(server)
            } catch (exception: Exception) {
                logDebug("Cannot fetch server name", exception)
            }
        }
        return getCachedServer(server)
    }

    fun clearCache() {
        cache.clear()
    }

}