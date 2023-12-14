package org.sirekanyan.outline.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.sirekanyan.outline.api.OutlineApi
import org.sirekanyan.outline.api.model.AccessKey
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.api.model.fromEntities
import org.sirekanyan.outline.api.model.toEntities
import org.sirekanyan.outline.db.KeyDao
import org.sirekanyan.outline.db.model.KeyEntity
import org.sirekanyan.outline.db.model.ServerEntity

class KeyRepository(private val api: OutlineApi, private val keyDao: KeyDao) {

    fun observeKeys(server: ServerEntity): Flow<List<Key>> =
        keyDao.observe(server).map(List<KeyEntity>::fromEntities)

    suspend fun updateKeys(server: ServerEntity) {
        val keys = api.getKeys(server)
        keyDao.update(server, keys.toEntities(server))
    }

    suspend fun renameKey(server: ServerEntity, accessKey: AccessKey, newName: String) {
        api.renameAccessKey(server, accessKey.id, newName)
    }

}