package com.github.livingwithhippos.torbox.kmp.client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TorrentDto(
    val id: Int,
    @SerialName("auth_id") val authId: String,
    val server: Int,
    val hash: String,
    val name: String,
    val magnet: String?,
    val size: Long,
    val active: Boolean,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("download_state") val downloadState: String,
    val seeds: Int,
    val peers: Int,
    val ratio: Double,
    val progress: Double,
    @SerialName("download_speed") val downloadSpeed: Long,
    @SerialName("upload_speed") val uploadSpeed: Long,
    val eta: Int,
    @SerialName("torrent_file") val torrentFile: Boolean,
    @SerialName("expires_at") val expiresAt: String?,
    @SerialName("download_present") val downloadPresent: Boolean,
    val files: List<TorrentFileDto>,
    @SerialName("download_path") val downloadPath: String,
    val availability: Int,
    @SerialName("download_finished") val downloadFinished: Boolean,
    val tracker: String?,
    @SerialName("total_uploaded") val totalUploaded: Long,
    @SerialName("total_downloaded") val totalDownloaded: Long,
    val cached: Boolean,
    val owner: String,
    @SerialName("seed_torrent") val seedTorrent: Boolean,
    @SerialName("allow_zipped") val allowZipped: Boolean,
    @SerialName("long_term_seeding") val longTermSeeding: Boolean,
    @SerialName("tracker_message") val trackerMessage: String?,
    @SerialName("cached_at") val cachedAt: String,
    @SerialName("private") val isPrivate: Boolean,
    @SerialName("alternative_hashes") val alternativeHashes: List<String>,
    val tags: List<String>,
)

@Serializable
data class TorrentFileDto(
    val id: Int,
    val md5: String?,
    val hash: String,
    val name: String,
    val size: Long,
    val zipped: Boolean,
    @SerialName("s3_path") val s3Path: String,
    val infected: Boolean,
    val mimetype: String,
    @SerialName("short_name") val shortName: String,
    @SerialName("absolute_path") val absolutePath: String,
    @SerialName("opensubtitles_hash") val opensubtitlesHash: String?,
)

@Serializable
data class TorrentInfoDto(
    val name: String,
    val hash: String,
    val size: Long,
    val trackers: List<String>,
    val seeds: Int,
    val peers: Int,
    val files: List<TorrentInfoFileDto>,
)

@Serializable
data class TorrentInfoFileDto(
    val name: String,
    val size: Long,
)

@Serializable
data class CreateTorrentDto(
    val hash: String,
    @SerialName("torrent_id") val torrentId: Long,
    @SerialName("auth_id") val authId: String,
)

@Serializable
data class TorrentControlRequestDto(
    @SerialName("torrent_id")
    val torrentId: Int? = null,
    val operation: String,
    val all: Boolean? = null,
)

@Serializable
data class EditTorrentRequestDto(
    @SerialName("torrent_id")
    val torrentId: Int,
    val name: String? = null,
    val tags: List<String>? = null,
    @SerialName("alternative_hashes")
    val alternativeHashes: List<String>? = null,
)

@Serializable
data class MagnetToFileRequestDto(
    val magnet: String,
)

/** Sealed result for exportTorrentData — caller gets either a magnet string or raw .torrent bytes */
sealed class ExportResultDto {
    data class Magnet(
        val magnet: String,
    ) : ExportResultDto()

    data class Torrent(
        val bytes: ByteArray,
    ) : ExportResultDto() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Torrent

            return bytes.contentEquals(other.bytes)
        }

        override fun hashCode(): Int = bytes.contentHashCode()
    }
}
