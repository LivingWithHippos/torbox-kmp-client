package com.github.livingwithhippos.torbox.network

/**
 * A basic configuration for the TorBox client.
 * The [logLevel] can be none (default), info, headers, body and all.
 * The bearer token may be printed with some of these levels, so only change them for debugging.
 */
data class RestClientConfig(
    var bearerToken: String,
    val requestTimeoutMs: Long = 60_000,
    val connectTimeoutMs: Long = 60_000,
    val socketTimeoutMs: Long = 60_000,
    val defaultHeaders: Map<String, String> = emptyMap(),
    val logLevel: String = "none",
)
