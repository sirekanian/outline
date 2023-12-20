package org.sirekanyan.outline.api.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.sirekanyan.outline.db.model.ServerEntity

fun createServerEntity(url: String, insecure: Boolean): Server =
    Server(url, insecure, name = "", traffic = null)

fun ServerEntity.fromEntity(): Server =
    Server(id, insecure, name, traffic)

fun List<ServerEntity>.fromEntities(): List<Server> =
    map(ServerEntity::fromEntity)

fun Server.toEntity(): ServerEntity =
    ServerEntity(id, insecure, name, traffic)

fun List<Server>.toEntities(): List<ServerEntity> =
    map(Server::toEntity)

@Parcelize
class Server(
    val id: String,
    val insecure: Boolean,
    val name: String,
    val traffic: Long?,
) : Parcelable {

    fun getHost(): String =
        Uri.parse(id).host.orEmpty()

}
