package org.sirekanyan.outline.api.model

import kotlinx.serialization.Serializable

@Serializable
class AccessKey(
    val id: String,
    val accessUrl: String,
    val name: String,
) {
    val defaultName: String
        get() = "Key $id"
    val nameOrDefault: String
        get() = name.ifEmpty { defaultName }
}