package org.sirekanyan.outline.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.sirekanyan.outline.api.model.AccessKeysResponse
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.api.model.ServerNameResponse
import org.sirekanyan.outline.api.model.TransferMetricsResponse

class OutlineApi {

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        engine { https.trustManager = InsecureTrustManager } // TODO: remove insecure http
    }

    suspend fun getServerName(apiUrl: String): String =
        httpClient.get("$apiUrl/server").body<ServerNameResponse>().name

    suspend fun getKeys(apiUrl: String): List<Key> {
        val accessKeys = getAccessKeys(apiUrl).accessKeys
        val transferMetrics = getTransferMetrics(apiUrl).bytesTransferredByUserId
        return accessKeys.map { accessKey -> Key(accessKey, transferMetrics[accessKey.id]) }
    }

    private suspend fun getAccessKeys(apiUrl: String): AccessKeysResponse =
        httpClient.get("$apiUrl/access-keys").body()

    private suspend fun getTransferMetrics(apiUrl: String): TransferMetricsResponse =
        httpClient.get("$apiUrl/metrics/transfer").body()

    suspend fun createAccessKey(apiUrl: String) {
        httpClient.post("$apiUrl/access-keys")
    }

}