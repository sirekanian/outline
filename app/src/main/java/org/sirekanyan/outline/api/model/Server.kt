package org.sirekanyan.outline.api.model

import android.net.Uri
import org.sirekanyan.outline.db.model.ServerEntity

fun createServerEntity(url: String, insecure: Boolean): ServerEntity =
    ServerEntity(url, insecure, name = "", traffic = null)

fun ServerEntity.getHost(): String =
    Uri.parse(id).host.orEmpty()
