package org.sirekanyan.outline.api.model

import kotlinx.serialization.Serializable

@Serializable
class AccessKey(
    val id: String,
    val accessUrl: String,
    val name: String,
)