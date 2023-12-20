package org.sirekanyan.outline.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.sirekanyan.outline.db.model.KeyEntity
import org.sirekanyan.outline.db.model.KeyWithServerEntity

fun List<Key>.toEntities(): List<KeyEntity> =
    map { key ->
        KeyEntity(key.server.id, key.id, key.accessUrl, key.name, key.traffic)
    }

fun List<KeyEntity>.fromEntities(server: Server): List<Key> =
    map { entity ->
        Key(server, entity.id, entity.url, entity.name, entity.traffic)
    }

fun List<KeyWithServerEntity>.fromEntities(): List<Key> =
    map { entity ->
        val server = Server(entity.serverId, entity.insecure, entity.serverName, entity.serverTraffic)
        Key(server, entity.id, entity.url, entity.name, entity.traffic)
    }

@Parcelize
class Key(
    val server: Server,
    val id: String,
    val accessUrl: String,
    val name: String,
    val traffic: Long?,
) : Parcelable {

    val defaultName: String
        get() = "Key $id"
    val nameOrDefault: String
        get() = name.ifEmpty { defaultName }

}
