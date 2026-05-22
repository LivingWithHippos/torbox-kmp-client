package com.github.livingwithhippos.torbox.kmp.client.network

import com.github.livingwithhippos.torbox.kmp.client.BASE_URL

class RestClientConfig(
    val baseUrl: String = BASE_URL,
    val bearerToken: String? = null,
    val requestTimeoutMs: Long = 60_000,
    val connectTimeoutMs: Long = 60_000,
    val socketTimeoutMs: Long = 60_000,
    val defaultHeaders: Map<String, String> = emptyMap(),
) {
}