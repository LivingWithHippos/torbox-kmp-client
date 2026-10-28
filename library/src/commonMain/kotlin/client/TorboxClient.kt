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
import com.github.livingwithhippos.torbox.network.TorboxAuthState
import com.github.livingwithhippos.torbox.network.TorboxClientConfig
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
    private val authState: TorboxAuthState,
) {
    companion object {
        fun create(
            config: TorboxClientConfig,
            authState: TorboxAuthState = TorboxAuthState(),
        ): TorboxClient {
            val client =
                createHttpClient(config = config, authState = authState, engineFactory = platformEngineFactory())
            return TorboxClient(
                torrent = TorrentApiImpl(client, config.dispatcher),
                web = WebApiImpl(client, config.dispatcher),
                usenet = UsenetApiImpl(client, config.dispatcher),
                general = GeneralApiImpl(client, config.dispatcher),
                notifications = NotificationsApiImpl(client, config.dispatcher),
                queued = QueuedApiImpl(client, config.dispatcher),
                user = UserApiImpl(client, config.dispatcher),
                rss = RSSApiImpl(client, config.dispatcher),
                integrations = IntegrationsApiImpl(client, config.dispatcher),
                stream = StreamApiImpl(client, config.dispatcher),
                httpClient = client,
                authState = authState,
            )
        }
    }

    fun updateBearerToken(token: String) {
        authState.bearerToken = token
    }

    fun close() = httpClient.close()
}
