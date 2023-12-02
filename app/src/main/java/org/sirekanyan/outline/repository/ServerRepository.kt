package org.sirekanyan.outline.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.sirekanyan.outline.api.OutlineApi
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.api.model.Server
import org.sirekanyan.outline.api.model.fromEntities
import org.sirekanyan.outline.api.model.toEntities
import org.sirekanyan.outline.db.KeyDao
import org.sirekanyan.outline.db.model.KeyEntity
import org.sirekanyan.outline.db.model.ServerEntity
import org.sirekanyan.outline.ext.logDebug
import java.util.concurrent.ConcurrentHashMap

class ServerRepository(private val api: OutlineApi, private val keyDao: KeyDao) {

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

    fun observeKeys(server: ServerEntity): Flow<List<Key>> =
        keyDao.observe(server).map(List<KeyEntity>::fromEntities)

    suspend fun updateKeys(server: ServerEntity) {
        val keys = api.getKeys(server)
        keyDao.update(server, keys.toEntities(server))
    }

}