package org.sirekanyan.outline.repository

import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.sirekanyan.outline.api.OutlineApi
import org.sirekanyan.outline.api.model.AccessKey
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.api.model.fromEntities
import org.sirekanyan.outline.api.model.toEntities
import org.sirekanyan.outline.db.KeyDao
import org.sirekanyan.outline.db.model.KeyWithServerEntity
import org.sirekanyan.outline.db.model.ServerEntity

class KeyRepository(private val api: OutlineApi, private val keyDao: KeyDao) {

    fun observeKeys(server: ServerEntity): Flow<List<Key>> =
        keyDao.observe(server).mapToList(IO).map { it.fromEntities(server) }

    fun observeAllKeys(): Flow<List<Key>> =
        keyDao.observeAll().mapToList(IO).map(List<KeyWithServerEntity>::fromEntities)

    suspend fun updateKeys(server: ServerEntity) {
        withContext(IO) {
            val keys = api.getKeys(server)
            keyDao.update(server, keys.toEntities())
        }
    }

    suspend fun renameKey(server: ServerEntity, accessKey: AccessKey, newName: String) {
        withContext(IO) {
            api.renameAccessKey(server, accessKey.id, newName)
        }
    }

}