package org.sirekanyan.outline.api.model

import org.sirekanyan.outline.db.model.KeyEntity
import org.sirekanyan.outline.db.model.ServerEntity

fun List<Key>.toEntities(server: ServerEntity): List<KeyEntity> =
    map { key ->
        val accessKey = key.accessKey
        KeyEntity(server.id, accessKey.id, accessKey.accessUrl, accessKey.name, key.traffic)
    }

fun List<KeyEntity>.fromEntities(): List<Key> =
    map { entity ->
        Key(AccessKey(entity.id, entity.url, entity.name), entity.traffic)
    }

class Key(val accessKey: AccessKey, val traffic: Long?)