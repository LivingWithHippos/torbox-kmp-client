package com.github.livingwithhippos.torbox.kmp.client.client

import com.github.livingwithhippos.torbox.kmp.client.api.TorrentsApi
import com.github.livingwithhippos.torbox.kmp.client.api.WebApi
import com.github.livingwithhippos.torbox.kmp.client.impl.TorboxApiImpl
import com.github.livingwithhippos.torbox.kmp.client.network.RestClientConfig
import com.github.livingwithhippos.torbox.kmp.client.network.createHttpClient
import com.github.livingwithhippos.torbox.kmp.client.network.platformEngineFactory
import io.ktor.client.HttpClient

class TorboxClient private constructor(
    val torrents: TorrentsApi,
    val web: WebApi,
    private val httpClient: HttpClient,
) {
    companion object {
        fun create(config: RestClientConfig): TorboxClient {
            val client = createHttpClient(config, platformEngineFactory())
            val groupedApi = TorboxApiImpl(client)
            return TorboxClient(
                torrents = groupedApi,
                web = groupedApi,
                httpClient = client,
            )
        }
    }

    fun close() = httpClient.close()
}
