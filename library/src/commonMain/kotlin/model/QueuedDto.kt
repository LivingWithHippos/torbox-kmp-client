package com.github.livingwithhippos.torbox.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QueuedDownloadDto(
    val id: Int,
    @SerialName("created_at") val createdAt: String,
    val magnet: String? = null,
    @SerialName("torrent_file") val torrentFile: String? = null,
    val hash: String,
    val name: String,
    val type: String,
    @SerialName("name_override") val nameOverride: String? = null,
    @SerialName("seed_torrent_override") val seedTorrentOverride: Int? = null,
)

@Serializable
data class ControlQueuedDownloadsRequestDto(
    @SerialName("queued_id") val queuedId: Int? = null,
    val operation: String,
    val all: Boolean? = null,
)
