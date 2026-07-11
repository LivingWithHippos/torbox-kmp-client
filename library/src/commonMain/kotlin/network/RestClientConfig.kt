package com.github.livingwithhippos.torbox.network

import com.github.livingwithhippos.torbox.BASE_URL

data class RestClientConfig(
    val bearerToken: String,
    val requestTimeoutMs: Long = 60_000,
    val connectTimeoutMs: Long = 60_000,
    val socketTimeoutMs: Long = 60_000,
    val defaultHeaders: Map<String, String> = emptyMap(),
    val logLevel: String = "none",
)
