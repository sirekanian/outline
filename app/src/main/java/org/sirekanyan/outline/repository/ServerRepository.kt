package org.sirekanyan.outline.repository

import android.net.Uri
import org.sirekanyan.outline.api.OutlineApi
import org.sirekanyan.outline.api.model.Server
import org.sirekanyan.outline.ext.logDebug
import java.util.concurrent.ConcurrentHashMap

class ServerRepository(private val api: OutlineApi) {

    private val cache: MutableMap<String, Server> = ConcurrentHashMap()

    fun getCachedServer(apiUrl: String): Server =
        cache[apiUrl] ?: Server(Uri.parse(apiUrl).host.orEmpty(), traffic = null)

    suspend fun fetchServer(apiUrl: String): Server =
        api.getServer(apiUrl).also { fetched ->
            cache[apiUrl] = fetched
        }

    suspend fun getServer(apiUrl: String): Server {
        if (!cache.containsKey(apiUrl)) {
            try {
                return fetchServer(apiUrl)
            } catch (exception: Exception) {
                logDebug("Cannot fetch server name", exception)
            }
        }
        return getCachedServer(apiUrl)
    }

}