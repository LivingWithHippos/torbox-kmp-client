package com.github.livingwithhippos.torbox.kmp.client.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.append
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.AttributeKey
import kotlinx.serialization.json.Json
import co.touchlab.kermit.Logger as KermitLogger
import io.ktor.client.plugins.logging.Logger as KtorLogger

val NoAuth = AttributeKey<Boolean>("NoAuth")

fun HttpRequestBuilder.noAuth() {
    attributes.put(NoAuth, true)
}

fun createHttpClient(
    config: RestClientConfig,
    engineFactory: HttpClientEngineFactory<*>,
): HttpClient =
    HttpClient(engineFactory) {
        expectSuccess = false

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    explicitNulls = false
                },
            )
        }

        if (config.logLevel != LogLevel.NONE) {
            install(Logging) {
                level = config.logLevel
                logger =
                    object : KtorLogger {
                        override fun log(message: String) {
                            KermitLogger.i(tag = "TorboxKMP") {
                                message
                            }
                        }
                    }
                // important: some APIs have the token as query parameter!
                sanitizeHeader { header -> header == HttpHeaders.Authorization }
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = config.requestTimeoutMs
            connectTimeoutMillis = config.connectTimeoutMs
            socketTimeoutMillis = config.socketTimeoutMs
        }

        defaultRequest {
            url(config.baseUrl)
            val skipAuth = attributes.getOrNull(NoAuth) == true
            if (!skipAuth) {
                config.bearerToken?.let { token ->
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            headers.append(HttpHeaders.Accept, ContentType.Application.Json)
            config.defaultHeaders.forEach { (k, v) -> headers.append(k, v) }
        }
    }
