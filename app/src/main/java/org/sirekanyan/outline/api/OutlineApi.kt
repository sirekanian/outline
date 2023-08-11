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

val API_URLS: List<Pair<String, String>> = listOf(
    // TODO: add api urls
)

class OutlineApi {

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        engine { https.trustManager = InsecureTrustManager } // TODO: remove insecure http
    }

    suspend fun getAccessKeys(index: Int): List<AccessKey> {
        val (_, apiUrl) = API_URLS.getOrNull(index) ?: return listOf()
        return httpClient.get("$apiUrl/access-keys").body<AccessKeysResponse>().accessKeys
    }

}