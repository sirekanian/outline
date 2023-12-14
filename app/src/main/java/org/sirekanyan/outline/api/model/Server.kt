package org.sirekanyan.outline.api.model

import android.net.Uri
import org.sirekanyan.outline.db.model.ServerEntity

fun ServerEntity.getHost(): String =
    Uri.parse(id).host.orEmpty()
