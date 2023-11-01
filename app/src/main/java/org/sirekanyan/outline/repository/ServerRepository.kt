package org.sirekanyan.outline.repository

import android.net.Uri
import org.sirekanyan.outline.api.OutlineApi
import org.sirekanyan.outline.api.model.Server
import org.sirekanyan.outline.db.model.ApiUrl
import org.sirekanyan.outline.ext.logDebug
import java.util.concurrent.ConcurrentHashMap

class ServerRepository(private val api: OutlineApi) {

    private val cache: MutableMap<String, Server> = ConcurrentHashMap()

    fun getCachedServer(apiUrl: ApiUrl): Server =
        cache[apiUrl.id] ?: Server(Uri.parse(apiUrl.id).host.orEmpty(), traffic = null)

    suspend fun fetchServer(apiUrl: ApiUrl): Server =
        api.getServer(apiUrl).also { fetched ->
            cache[apiUrl.id] = fetched
        }

    suspend fun getServer(apiUrl: ApiUrl): Server {
        if (!cache.containsKey(apiUrl.id)) {
            try {
                return fetchServer(apiUrl)
            } catch (exception: Exception) {
                logDebug("Cannot fetch server name", exception)
            }
        }
        return getCachedServer(apiUrl)
    }

}