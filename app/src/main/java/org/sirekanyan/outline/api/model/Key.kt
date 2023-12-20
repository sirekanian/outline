package org.sirekanyan.outline.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.sirekanyan.outline.api.model.Key.AccessKey
import org.sirekanyan.outline.db.model.KeyEntity
import org.sirekanyan.outline.db.model.KeyWithServerEntity

fun List<Key>.toEntities(): List<KeyEntity> =
    map { key ->
        val accessKey = key.accessKey
        KeyEntity(key.server.id, accessKey.id, accessKey.accessUrl, accessKey.name, key.traffic)
    }

fun List<KeyEntity>.fromEntities(server: Server): List<Key> =
    map { entity ->
        Key(server, AccessKey(entity.id, entity.url, entity.name), entity.traffic)
    }

fun List<KeyWithServerEntity>.fromEntities(): List<Key> =
    map { entity ->
        Key(
            Server(entity.serverId, entity.insecure, entity.serverName, entity.serverTraffic),
            AccessKey(entity.id, entity.url, entity.name),
            entity.traffic,
        )
    }

@Parcelize
class Key(val server: Server, val accessKey: AccessKey, val traffic: Long?) : Parcelable {

    @Parcelize
    class AccessKey(
        val id: String,
        val accessUrl: String,
        val name: String,
    ) : Parcelable {

        val defaultName: String
            get() = "Key $id"
        val nameOrDefault: String
            get() = name.ifEmpty { defaultName }

    }

}