package org.sirekanyan.outline.api.model

import kotlinx.serialization.Serializable

@Serializable
class TransferMetricsResponse(val bytesTransferredByUserId: Map<String, Long>)