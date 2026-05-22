package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.CreateTorrentResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.CreateWebDownloadResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.DownloadTorrentResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentAvailabilityListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentAvailabilityObjectResponseDto
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
        addOnlyIfCached: Boolean? = null,
    ): ApiResult<CreateTorrentResponseDto>

    suspend fun downloadTorrent(
        token: String,
        torrentId: Int,
        fileId: Int?= null,
        zipLink: String? = null,
        userIp: String? = null,
        redirect: Boolean? = null,
        appendName: Boolean? = null,
    ): ApiResult<DownloadTorrentResponseDto>

    suspend fun getTorrentAvailabilityList(
        hashes: List<String>,
        listFiles: Boolean?= null
    ): ApiResult<TorrentAvailabilityListResponseDto>

    suspend fun getTorrentAvailabilityObject(
        hashes: List<String>,
        listFiles: Boolean?= null
    ): ApiResult<TorrentAvailabilityObjectResponseDto>

    suspend fun downloadWebLink(
        link: String,
        password: String? = null,
        name: String? = null,
        asQueued: Boolean? = null,
        addOnlyIfCached: Boolean? = null,
    ): ApiResult<CreateWebDownloadResponseDto>
}