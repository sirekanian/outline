package org.sirekanyan.outline.repository

import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.sirekanyan.outline.api.OutlineApi
import org.sirekanyan.outline.api.model.fromEntities
import org.sirekanyan.outline.api.model.toEntities
import org.sirekanyan.outline.api.model.toEntity
import org.sirekanyan.outline.db.ServerDao
import org.sirekanyan.outline.ext.logDebug
import org.sirekanyan.outline.api.model.Server as ServerEntity

class ServerRepository(private val api: OutlineApi, private val serverDao: ServerDao) {

    fun observeServers(): Flow<List<ServerEntity>> =
        serverDao.observeAll().mapToList(IO).map { it.fromEntities() }

    suspend fun updateServers(servers: List<ServerEntity>) {
        withContext(IO) {
            val deferredServers = servers.map {
                async {
                    try {
                        api.getServer(it)
                    } catch (exception: Exception) {
                        logDebug("Cannot get server", exception)
                        null
                    }
                }
            }
            serverDao.insertAll(deferredServers.awaitAll().filterNotNull().toEntities())
        }
    }

    suspend fun updateServer(server: ServerEntity): ServerEntity =
        withContext(IO) {
            refreshServer(server)
        }

    suspend fun renameServer(server: ServerEntity, newName: String) {
        withContext(IO) {
            api.renameServer(server, newName)
            refreshServer(server)
        }
    }

    private suspend fun refreshServer(server: ServerEntity): ServerEntity =
        api.getServer(server).also { newServer ->
            serverDao.insert(newServer.toEntity())
        }

}