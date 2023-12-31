package org.sirekanyan.outline.api.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.sirekanyan.outline.db.model.ServerEntity
import org.sirekanyan.outline.text.formatCount
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

fun List<Server>.getTotalBadgeText(isCount: Boolean): String? {
    val totalCount = sumOf { it.count ?: 0 }
    val totalTraffic = sumOf { it.traffic ?: 0 }
    return if (totalCount > 0 && totalTraffic > 0) {
        if (isCount) formatCount(totalCount) else formatTraffic(totalTraffic)
    } else {
        null
    }
}

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
            count?.let(::formatCount)
        } else {
            traffic?.let(::formatTraffic)
        }

}
