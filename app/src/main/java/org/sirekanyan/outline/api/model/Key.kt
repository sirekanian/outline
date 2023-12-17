package org.sirekanyan.outline.api.model

import org.sirekanyan.outline.db.model.KeyEntity
import org.sirekanyan.outline.db.model.KeyWithServerEntity
import org.sirekanyan.outline.db.model.ServerEntity

fun List<Key>.toEntities(): List<KeyEntity> =
    map { key ->
        val accessKey = key.accessKey
        KeyEntity(key.server.id, accessKey.id, accessKey.accessUrl, accessKey.name, key.traffic)
    }

fun List<KeyEntity>.fromEntities(server: ServerEntity): List<Key> =
    map { entity ->
        Key(server, AccessKey(entity.id, entity.url, entity.name), entity.traffic)
    }

fun List<KeyWithServerEntity>.fromEntities(): List<Key> =
    map { entity ->
        Key(
            ServerEntity(entity.serverId, entity.insecure, entity.serverName, entity.serverTraffic),
            AccessKey(entity.id, entity.url, entity.name),
            entity.traffic,
        )
    }

class Key(val server: ServerEntity, val accessKey: AccessKey, val traffic: Long?)