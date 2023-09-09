package org.sirekanyan.outline.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.sirekanyan.outline.api.model.AccessKeysResponse
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.api.model.RenameRequest
import org.sirekanyan.outline.api.model.Server
import org.sirekanyan.outline.api.model.ServerNameResponse
import org.sirekanyan.outline.api.model.TransferMetricsResponse
import org.sirekanyan.outline.ext.logDebug

class OutlineApi {

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        engine { https.trustManager = InsecureTrustManager } // TODO: remove insecure http
    }

    suspend fun getServer(apiUrl: String): Server {
        val name = httpClient.get("$apiUrl/server").body<ServerNameResponse>().name
        val transferMetrics = getTransferMetrics(apiUrl)?.bytesTransferredByUserId
        return Server(name, transferMetrics?.values?.sum())
    }

    suspend fun getKeys(apiUrl: String): List<Key> {
        val accessKeys = getAccessKeys(apiUrl).accessKeys
        val transferMetrics = getTransferMetrics(apiUrl)?.bytesTransferredByUserId
        return accessKeys.map { accessKey -> Key(accessKey, transferMetrics?.get(accessKey.id)) }
    }

    private suspend fun getAccessKeys(apiUrl: String): AccessKeysResponse =
        httpClient.get("$apiUrl/access-keys").body()

    private suspend fun getTransferMetrics(apiUrl: String): TransferMetricsResponse? =
        try {
            httpClient.get("$apiUrl/metrics/transfer").body()
        } catch (exception: Exception) {
            logDebug("Cannot fetch transfer metrics", exception)
            null
        }

    suspend fun createAccessKey(apiUrl: String) {
        httpClient.post("$apiUrl/access-keys")
    }

    suspend fun renameAccessKey(apiUrl: String, id: String, name: String) {
        httpClient.put("$apiUrl/access-keys/$id/name") {
            contentType(ContentType.Application.Json)
            setBody(RenameRequest(name))
        }
    }

    suspend fun deleteAccessKey(apiUrl: String, id: String) {
        httpClient.delete("$apiUrl/access-keys/$id")
    }

}