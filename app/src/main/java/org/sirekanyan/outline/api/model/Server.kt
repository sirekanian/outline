package org.sirekanyan.outline.api.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.sirekanyan.outline.db.model.ServerEntity
import org.sirekanyan.outline.text.formatTraffic

fun createServerEntity(url: String, insecure: Boolean): Server =
    Server(url, insecure, name = "", traffic = null, count = null)

fun ServerEntity.fromEntity(): Server =
    Server(id, insecure, name, traffic, count)

fun List<ServerEntity>.fromEntities(): List<Server> =
    map(ServerEntity::fromEntity)

fun Server.toEntity(): ServerEntity =
    ServerEntity(id, insecure, name, traffic, count)

fun List<Server>.toEntities(): List<ServerEntity> =
    map(Server::toEntity)

@Parcelize
class Server(
    val id: String,
    val insecure: Boolean,
    val name: String,
    val traffic: Long?,
    val count: Long?,
) : Parcelable {

    fun getHost(): String =
        Uri.parse(id).host.orEmpty()

    fun getBadgeText(isCount: Boolean): String? =
        if (isCount) {
            count?.let { "$it ${if (it == 1L) "key" else "keys"}" }
        } else {
            traffic?.let(::formatTraffic)
        }

}
