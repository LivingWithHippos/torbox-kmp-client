package com.github.livingwithhippos.torbox.kmp.client.client

import com.github.livingwithhippos.torbox.kmp.client.api.GeneralApi
import com.github.livingwithhippos.torbox.kmp.client.api.NotificationsApi
import com.github.livingwithhippos.torbox.kmp.client.api.RSSApi
import com.github.livingwithhippos.torbox.kmp.client.api.TorrentsApi
import com.github.livingwithhippos.torbox.kmp.client.api.UsenetApi
import com.github.livingwithhippos.torbox.kmp.client.api.UserApi
import com.github.livingwithhippos.torbox.kmp.client.api.WebApi
import com.github.livingwithhippos.torbox.kmp.client.impl.TorboxApiImpl
import com.github.livingwithhippos.torbox.kmp.client.network.RestClientConfig
import com.github.livingwithhippos.torbox.kmp.client.network.createHttpClient
import com.github.livingwithhippos.torbox.kmp.client.network.platformEngineFactory
import io.ktor.client.HttpClient

class TorboxClient private constructor(
    val torrents: TorrentsApi,
    val web: WebApi,
    val usenet: UsenetApi,
    val general: GeneralApi,
    val notifications: NotificationsApi,
    val user: UserApi,
    val rss: RSSApi,
    private val httpClient: HttpClient,
) {
    companion object {
        fun create(config: RestClientConfig): TorboxClient {
            val client = createHttpClient(config, platformEngineFactory())
            // todo: since we split this rename all APIs to a shorter version, like torrents.create() etc.
            val groupedApi = TorboxApiImpl(client)
            return TorboxClient(
                torrents = groupedApi,
                web = groupedApi,
                usenet = groupedApi,
                general = groupedApi,
                notifications = groupedApi,
                user = groupedApi,
                rss = groupedApi,
                httpClient = client,
            )
        }
    }

    fun close() = httpClient.close()
}
