package org.sirekanyan.outline.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
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
import org.sirekanyan.outline.ext.logDebug
import java.security.SecureRandom
import javax.net.ssl.SSLContext

private fun createOkHttpClient(block: OkHttpConfig.() -> Unit = {}): HttpClient =
    HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        engine {
            block()
        }
    }

private fun setInsecureHttp(builder: OkHttpClient.Builder) {
    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, arrayOf(InsecureTrustManager), SecureRandom())
    builder.sslSocketFactory(sslContext.socketFactory, InsecureTrustManager)
    builder.hostnameVerifier { _, _ -> true }
}

class OutlineApi {

    private val httpClient = createOkHttpClient()
    private val insecureHttpClient = createOkHttpClient {
        config {
            setInsecureHttp(this)
        }
    }

    private suspend fun request(
        httpMethod: HttpMethod,
        server: Server,
        path: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ): HttpResponse {
        val client = if (server.insecure) insecureHttpClient else httpClient
        return client.request(server.id + '/' + path) { method = httpMethod; block() }
    }

    suspend fun getServer(server: Server): Server {
        val name = request(HttpMethod.Get, server, "server").body<ServerNameResponse>().name
        return Server(server.id, server.insecure, name, server.traffic, server.count)
    }

    suspend fun renameServer(server: Server, name: String) {
        request(HttpMethod.Put, server, "name") {
            contentType(ContentType.Application.Json)
            setBody(RenameRequest(name))
        }
    }

    suspend fun getKeys(server: Server): List<Key> {
        val accessKeys = getAccessKeys(server).accessKeys
        val transferMetrics = getTransferMetrics(server)?.bytesTransferredByUserId
        return accessKeys.map {
            Key(server, it.id, it.accessUrl, it.name, transferMetrics?.get(it.id))
        }
    }

    private suspend fun getAccessKeys(server: Server): AccessKeysResponse =
        request(HttpMethod.Get, server, "access-keys").body()

    private suspend fun getTransferMetrics(server: Server): TransferMetricsResponse? =
        try {
            request(HttpMethod.Get, server, "metrics/transfer").body()
        } catch (exception: Exception) {
            logDebug("Cannot fetch transfer metrics", exception)
            null
        }

    suspend fun createAccessKey(server: Server) {
        request(HttpMethod.Post, server, "access-keys")
    }

    suspend fun renameAccessKey(server: Server, id: String, name: String) {
        request(HttpMethod.Put, server, "access-keys/$id/name") {
            contentType(ContentType.Application.Json)
            setBody(RenameRequest(name))
        }
    }

    suspend fun deleteAccessKey(server: Server, id: String) {
        request(HttpMethod.Delete, server, "access-keys/$id")
    }

}