package com.github.livingwithhippos.torbox.kmp.client.client

import com.github.livingwithhippos.torbox.kmp.client.api.TorboxApi
import com.github.livingwithhippos.torbox.kmp.client.impl.TorboxApiImpl
import com.github.livingwithhippos.torbox.kmp.client.network.RestClientConfig
import com.github.livingwithhippos.torbox.kmp.client.network.createHttpClient
import com.github.livingwithhippos.torbox.kmp.client.network.platformEngineFactory
import io.ktor.client.HttpClient

class TorboxClient private constructor(
    // todo: split implementations in categories? torrents, web etc?
    val api: TorboxApi,
    private val httpClient: HttpClient,
) {
    companion object {
        fun create(config: RestClientConfig): TorboxClient {
            val client = createHttpClient(config, platformEngineFactory())
            return TorboxClient(
                api = TorboxApiImpl(client),
                httpClient = client
            )
        }
    }

    fun close() = httpClient.close()
}