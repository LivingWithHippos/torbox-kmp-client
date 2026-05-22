package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.CreateTorrentResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface TorboxApi {
    suspend fun getTorrents(
        bypassCache: String? = null,
        offset: Int? = null,
        limit: Int? = null
    ): ApiResult<TorrentListResponseDto>

    suspend fun createTorrent(
        file: String? = null,
        magnet: String? = null,
        seed: Int = 1,
        allowZip: Boolean,
        name: String? = null,
        asQueued: Boolean? = null,
        addOnlyIfCached: Boolean = false,
    ): ApiResult<CreateTorrentResponseDto>
}