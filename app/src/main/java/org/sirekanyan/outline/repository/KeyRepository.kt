package org.sirekanyan.outline.repository

import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.sirekanyan.outline.api.OutlineApi
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.api.model.Server
import org.sirekanyan.outline.api.model.fromEntities
import org.sirekanyan.outline.api.model.toEntities
import org.sirekanyan.outline.db.KeyDao
import org.sirekanyan.outline.db.ServerDao
import org.sirekanyan.outline.db.model.KeyWithServerEntity

class KeyRepository(
    private val api: OutlineApi,
    private val keyDao: KeyDao,
    private val serverDao: ServerDao,
) {

    fun observeKeys(server: Server): Flow<List<Key>> =
        keyDao.observe(server).mapToList(IO).map { it.fromEntities(server) }

    fun observeAllKeys(query: String): Flow<List<Key>> =
        keyDao.observeAll(query).mapToList(IO).map(List<KeyWithServerEntity>::fromEntities)

    suspend fun updateKeys(server: Server) {
        withContext(IO) {
            val keys = api.getKeys(server)
            keyDao.update(server, keys.toEntities())
            serverDao.update(
                id = server.id,
                traffic = keys.sumOf { it.traffic ?: 0 },
                count = keys.size.toLong(),
            )
        }
    }

    suspend fun createKey(server: Server) {
        withContext(IO) {
            api.createAccessKey(server)
        }
    }

    suspend fun renameKey(server: Server, key: Key, newName: String) {
        withContext(IO) {
            api.renameAccessKey(server, key.id, newName)
        }
    }

    suspend fun deleteKey(key: Key) {
        withContext(IO) {
            api.deleteAccessKey(key.server, key.id)
        }
    }

}