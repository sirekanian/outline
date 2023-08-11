package org.sirekanyan.outline.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.sirekanyan.outline.api.model.AccessKey
import org.sirekanyan.outline.api.model.AccessKeysResponse

private const val API_URL = "" // TODO: add api url

class OutlineApi {

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        engine { https.trustManager = InsecureTrustManager } // TODO: remove insecure http
    }

    suspend fun getAccessKeys(): List<AccessKey> =
        httpClient.get("$API_URL/access-keys").body<AccessKeysResponse>().accessKeys

}