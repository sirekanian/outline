package org.sirekanyan.outline.api.model

import kotlinx.serialization.Serializable

@Serializable
class AccessKeysResponse(val accessKeys: List<AccessKey>)