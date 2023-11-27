package org.sirekanyan.outline.repository

import android.net.Uri
import org.sirekanyan.outline.api.OutlineApi
import org.sirekanyan.outline.api.model.Server
import org.sirekanyan.outline.db.model.ServerEntity
import org.sirekanyan.outline.ext.logDebug
import java.util.concurrent.ConcurrentHashMap

class ServerRepository(private val api: OutlineApi) {

    private val cache: MutableMap<String, Server> = ConcurrentHashMap()

    fun getCachedServer(server: ServerEntity): Server =
        cache[server.id] ?: Server(Uri.parse(server.id).host.orEmpty(), traffic = null)

    suspend fun fetchServer(server: ServerEntity): Server =
        api.getServer(server).also { fetched ->
            cache[server.id] = fetched
        }

    suspend fun getServer(server: ServerEntity): Server {
        if (!cache.containsKey(server.id)) {
            try {
                return fetchServer(server)
            } catch (exception: Exception) {
                logDebug("Cannot fetch server name", exception)
            }
        }
        return getCachedServer(server)
    }

}