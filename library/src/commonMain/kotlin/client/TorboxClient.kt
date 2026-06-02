package com.github.livingwithhippos.torbox.client

import com.github.livingwithhippos.torbox.api.GeneralApi
import com.github.livingwithhippos.torbox.api.IntegrationsApi
import com.github.livingwithhippos.torbox.api.NotificationsApi
import com.github.livingwithhippos.torbox.api.QueuedApi
import com.github.livingwithhippos.torbox.api.RSSApi
import com.github.livingwithhippos.torbox.api.StreamApi
import com.github.livingwithhippos.torbox.api.TorrentApi
import com.github.livingwithhippos.torbox.api.UsenetApi
import com.github.livingwithhippos.torbox.api.UserApi
import com.github.livingwithhippos.torbox.api.WebApi
import com.github.livingwithhippos.torbox.impl.GeneralApiImpl
import com.github.livingwithhippos.torbox.impl.IntegrationsApiImpl
import com.github.livingwithhippos.torbox.impl.NotificationsApiImpl
import com.github.livingwithhippos.torbox.impl.QueuedApiImpl
import com.github.livingwithhippos.torbox.impl.RSSApiImpl
import com.github.livingwithhippos.torbox.impl.StreamApiImpl
import com.github.livingwithhippos.torbox.impl.TorrentApiImpl
import com.github.livingwithhippos.torbox.impl.UsenetApiImpl
import com.github.livingwithhippos.torbox.impl.UserApiImpl
import com.github.livingwithhippos.torbox.impl.WebApiImpl
import com.github.livingwithhippos.torbox.network.RestClientConfig
import com.github.livingwithhippos.torbox.network.createHttpClient
import com.github.livingwithhippos.torbox.network.platformEngineFactory
import io.ktor.client.HttpClient

class TorboxClient private constructor(
    val torrent: TorrentApi,
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
                torrent = TorrentApiImpl(client),
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
