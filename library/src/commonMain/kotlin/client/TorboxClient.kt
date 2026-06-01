package com.github.livingwithhippos.torbox.kmp.client.client

import com.github.livingwithhippos.torbox.kmp.client.api.GeneralApi
import com.github.livingwithhippos.torbox.kmp.client.api.IntegrationsApi
import com.github.livingwithhippos.torbox.kmp.client.api.NotificationsApi
import com.github.livingwithhippos.torbox.kmp.client.api.QueuedApi
import com.github.livingwithhippos.torbox.kmp.client.api.RSSApi
import com.github.livingwithhippos.torbox.kmp.client.api.StreamApi
import com.github.livingwithhippos.torbox.kmp.client.api.TorrentsApi
import com.github.livingwithhippos.torbox.kmp.client.api.UsenetApi
import com.github.livingwithhippos.torbox.kmp.client.api.UserApi
import com.github.livingwithhippos.torbox.kmp.client.api.WebApi
import com.github.livingwithhippos.torbox.kmp.client.impl.GeneralApiImpl
import com.github.livingwithhippos.torbox.kmp.client.impl.IntegrationsApiImpl
import com.github.livingwithhippos.torbox.kmp.client.impl.NotificationsApiImpl
import com.github.livingwithhippos.torbox.kmp.client.impl.QueuedApiImpl
import com.github.livingwithhippos.torbox.kmp.client.impl.RSSApiImpl
import com.github.livingwithhippos.torbox.kmp.client.impl.StreamApiImpl
import com.github.livingwithhippos.torbox.kmp.client.impl.TorrentsApiImpl
import com.github.livingwithhippos.torbox.kmp.client.impl.UsenetApiImpl
import com.github.livingwithhippos.torbox.kmp.client.impl.UserApiImpl
import com.github.livingwithhippos.torbox.kmp.client.impl.WebApiImpl
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
    val queued: QueuedApi,
    val user: UserApi,
    val rss: RSSApi,
    val integrations: IntegrationsApi,
    val stream: StreamApi,
    private val httpClient: HttpClient,
) {
    companion object {
        fun create(config: RestClientConfig): TorboxClient {
            val client = createHttpClient(config, platformEngineFactory())
            return TorboxClient(
                torrents = TorrentsApiImpl(client),
                web = WebApiImpl(client),
                usenet = UsenetApiImpl(client),
                general = GeneralApiImpl(client),
                notifications = NotificationsApiImpl(client),
                queued = QueuedApiImpl(client),
                user = UserApiImpl(client),
                rss = RSSApiImpl(client),
                integrations = IntegrationsApiImpl(client),
                stream = StreamApiImpl(client),
                httpClient = client,
            )
        }
    }

    fun close() = httpClient.close()
}
