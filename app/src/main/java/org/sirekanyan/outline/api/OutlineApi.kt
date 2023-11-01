package org.sirekanyan.outline.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import org.sirekanyan.outline.api.model.AccessKeysResponse
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.api.model.RenameRequest
import org.sirekanyan.outline.api.model.Server
import org.sirekanyan.outline.api.model.ServerNameResponse
import org.sirekanyan.outline.api.model.TransferMetricsResponse
import org.sirekanyan.outline.db.model.ApiUrl
import org.sirekanyan.outline.ext.logDebug
import java.security.SecureRandom
import javax.net.ssl.SSLContext

private fun setInsecureHttp(builder: OkHttpClient.Builder) {
    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, arrayOf(InsecureTrustManager), SecureRandom())
    builder.sslSocketFactory(sslContext.socketFactory, InsecureTrustManager)
    builder.hostnameVerifier { _, _ -> true }
}

class OutlineApi {

    private val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        engine {
            config {
                setInsecureHttp(this) // TODO: remove insecure http
            }
        }
    }

    private suspend fun request(
        httpMethod: HttpMethod,
        apiUrl: ApiUrl,
        path: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ): HttpResponse {
        return httpClient.request(apiUrl.id + '/' + path) { method = httpMethod; block() }
    }

    suspend fun getServer(apiUrl: ApiUrl): Server {
        val name = request(HttpMethod.Get, apiUrl, "server").body<ServerNameResponse>().name
        val transferMetrics = getTransferMetrics(apiUrl)?.bytesTransferredByUserId
        return Server(name, transferMetrics?.values?.sum())
    }

    suspend fun getKeys(apiUrl: ApiUrl): List<Key> {
        val accessKeys = getAccessKeys(apiUrl).accessKeys
        val transferMetrics = getTransferMetrics(apiUrl)?.bytesTransferredByUserId
        return accessKeys.map { accessKey -> Key(accessKey, transferMetrics?.get(accessKey.id)) }
    }

    private suspend fun getAccessKeys(apiUrl: ApiUrl): AccessKeysResponse =
        request(HttpMethod.Get, apiUrl, "access-keys").body()

    private suspend fun getTransferMetrics(apiUrl: ApiUrl): TransferMetricsResponse? =
        try {
            request(HttpMethod.Get, apiUrl, "metrics/transfer").body()
        } catch (exception: Exception) {
            logDebug("Cannot fetch transfer metrics", exception)
            null
        }

    suspend fun createAccessKey(apiUrl: ApiUrl) {
        request(HttpMethod.Post, apiUrl, "access-keys")
    }

    suspend fun renameAccessKey(apiUrl: ApiUrl, id: String, name: String) {
        request(HttpMethod.Put, apiUrl, "access-keys/$id/name") {
            contentType(ContentType.Application.Json)
            setBody(RenameRequest(name))
        }
    }

    suspend fun deleteAccessKey(apiUrl: ApiUrl, id: String) {
        request(HttpMethod.Delete, apiUrl, "access-keys/$id")
    }

}