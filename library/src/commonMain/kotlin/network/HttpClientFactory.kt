package com.github.livingwithhippos.torbox.network

import com.github.livingwithhippos.torbox.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.api.createClientPlugin
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

internal val NoAuth = AttributeKey<Boolean>("NoAuth")

/** Marks a request so that the bearer token is not sent */
internal fun HttpRequestBuilder.noAuth() {
    attributes.put(NoAuth, true)
}

internal fun logLevelToEnum(level: String): LogLevel =
    when (level.lowercase()) {
        "none" -> LogLevel.NONE
        "info" -> LogLevel.INFO
        "headers" -> LogLevel.HEADERS
        "body" -> LogLevel.BODY
        "all" -> LogLevel.ALL
        else -> LogLevel.NONE
    }

/**
 * Adds the bearer token to each request, unless it's blank or the request is marked with [noAuth].
 * This runs after the request builder, so the [NoAuth] attribute set there is visible.
 */
private fun bearerAuthPlugin(authState: TorboxAuthState) =
    createClientPlugin("TorboxBearerAuth") {
        onRequest { request, _ ->
            val skipAuth =
                request.attributes.getOrNull(NoAuth) == true ||
                    authState.bearerToken.isBlank() ||
                    request.headers.contains(HttpHeaders.Authorization)
            if (!skipAuth) {
                request.header(HttpHeaders.Authorization, "Bearer ${authState.bearerToken}")
            }
        }
    }

internal fun createHttpClient(
    config: TorboxClientConfig,
    authState: TorboxAuthState,
    engineFactory: HttpClientEngineFactory<*>,
): HttpClient =
    HttpClient(engineFactory) {
        configureTorbox(config, authState)
    }

internal fun HttpClientConfig<*>.configureTorbox(
    config: TorboxClientConfig,
    authState: TorboxAuthState,
) {
    expectSuccess = false

    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                isLenient = true
                explicitNulls = false
                coerceInputValues = true
            },
        )
    }

    val logLevel = logLevelToEnum(config.logLevel)

    if (logLevel != LogLevel.NONE) {
        install(Logging) {
            level = logLevel
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

    install(bearerAuthPlugin(authState))

    defaultRequest {
        url(BASE_URL)
        headers.append(HttpHeaders.Accept, ContentType.Application.Json)
        config.defaultHeaders.forEach { (k, v) -> headers.append(k, v) }
    }
}
