package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.CreateTorrentResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.DownloadResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.ExportTorrentResultDto
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentAvailabilityListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentAvailabilityObjectResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface TorrentsApi {
    suspend fun asyncCreateTorrent(
        fileBytes: ByteArray? = null,
        fileName: String? = null,
        magnet: String? = null,
        seed: Int? = null,
        allowZip: Boolean? = null,
        name: String? = null,
        asQueued: Boolean? = null,
        addOnlyIfCached: Boolean? = null,
    ): ApiResult<GenericResponseDto>

    suspend fun createTorrent(
        fileBytes: ByteArray? = null,
        fileName: String? = null,
        magnet: String? = null,
        seed: Int? = null,
        allowZip: Boolean? = null,
        name: String? = null,
        asQueued: Boolean? = null,
        addOnlyIfCached: Boolean? = null,
    ): ApiResult<CreateTorrentResponseDto>

    /**
     * Controls a torrent. By sending the torrent's ID and the type of operation you want to perform, it will send that request to the torrent client.
     *
     * https://api-docs.torbox.app/#7192d165-a4f0-4588-9792-1b54766a1447
     */
    suspend fun controlTorrent(
        torrentId: Int? = null,
        operation: String,
        all: Boolean? = null,
    ): ApiResult<GenericResponseDto>

    /**
     * Updates mutable metadata for a cached torrent item.
     * Any provided field replaces the previous value for that field.
     *
     * https://api.torbox.app/v1/api/torrents/edittorrent
     */
    suspend fun editTorrent(
        torrentId: Int,
        name: String? = null,
        tags: List<String>? = null,
        alternativeHashes: List<String>? = null,
    ): ApiResult<GenericResponseDto>

    suspend fun getTorrents(
        bypassCache: String? = null,
        id: Int? = null,
        offset: Int? = null,
        limit: Int? = null,
    ): ApiResult<TorrentListResponseDto>

    suspend fun requestTorrentDownloadLink(
        token: String,
        torrentId: Int,
        fileId: Int? = null,
        zipLink: String? = null,
        userIp: String? = null,
        redirect: Boolean? = null,
        appendName: Boolean? = null,
    ): ApiResult<DownloadResponseDto>

    suspend fun getTorrentAvailabilityList(
        hashes: List<String>,
        listFiles: Boolean? = null,
    ): ApiResult<TorrentAvailabilityListResponseDto>

    suspend fun getTorrentAvailabilityObject(
        hashes: List<String>,
        listFiles: Boolean? = null,
    ): ApiResult<TorrentAvailabilityObjectResponseDto>

    suspend fun getTorrentAvailabilityBatchObject(hashes: List<String>): ApiResult<TorrentAvailabilityObjectResponseDto>

    suspend fun getTorrentAvailabilityBatchList(hashes: List<String>): ApiResult<TorrentAvailabilityListResponseDto>

    /**
     * Exports a torrent as a magnet link or a .torrent file.
     * - type = "magnet" -> [ExportTorrentResultDto.Magnet]
     * - type = "file"   -> [ExportTorrentResultDto.File]
     *
     * https://api.torbox.app/v1/api/torrents/exportdata
     */
    suspend fun exportTorrentData(
        torrentId: Int,
        type: String,
    ): ApiResult<ExportTorrentResultDto>

    /**
     * Converts a magnet link to its corresponding .torrent file bytes.
     * This endpoint does not require authorization.
     *
     * https://api.torbox.app/v1/api/torrents/magnettofile
     */
    suspend fun magnetToFile(magnet: String): ApiResult<ByteArray>
}

