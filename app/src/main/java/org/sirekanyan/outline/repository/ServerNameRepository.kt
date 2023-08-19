package org.sirekanyan.outline.repository

import android.net.Uri
import android.util.Log
import org.sirekanyan.outline.api.OutlineApi
import java.util.concurrent.ConcurrentHashMap

class ServerNameRepository(private val api: OutlineApi) {

    private val cache: MutableMap<String, String> = ConcurrentHashMap()

    fun getDefaultName(apiUrl: String): String =
        cache[apiUrl] ?: Uri.parse(apiUrl).host.orEmpty()

    suspend fun fetchName(apiUrl: String): String =
        api.getServerName(apiUrl).also { fetched ->
            cache[apiUrl] = fetched
        }

    suspend fun getName(apiUrl: String): String {
        if (!cache.containsKey(apiUrl)) {
            try {
                return fetchName(apiUrl)
            } catch (exception: Exception) {
                Log.d("OUTLINE", "Cannot fetch server name", exception)
            }
        }
        return getDefaultName(apiUrl)
    }

}