package com.github.livingwithhippos.torbox.kmp.client.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject

@Serializable
data class TorrentListResponseDto(
    val success: Boolean,
    val error: String?,
    val detail: String,
    @Serializable(with = TorrentDataSerializer::class)
    val data: List<TorrentDto>?,
)

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
data class CreateTorrentResponseDto(
    val success: Boolean,
    val error: String?,
    val detail: String,
    val data: CreateTorrentDto?,
)

@Serializable
data class CreateTorrentDto(
    val hash: String,
    @SerialName("torrent_id") val torrentId: Long,
    @SerialName("auth_id") val authId: String,
)

@Serializable
data class TorrentAvailabilityObjectResponseDto(
    val success: Boolean,
    val error: String?,
    val detail: String,
    val data: Map<String, TorrentAvailabilityDto>?,
)

@Serializable
data class TorrentAvailabilityListResponseDto(
    val success: Boolean,
    val error: String?,
    val detail: String,
    val data: List<TorrentAvailabilityDto>?,
)

@Serializable
data class TorrentAvailabilityDto(
    val name: String,
    val size: Long,
    val hash: String,
)

@Serializable
object TorrentDataSerializer : KSerializer<List<TorrentDto>?> {
    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

    override fun deserialize(decoder: Decoder): List<TorrentDto>? {
        val jsonDecoder =
            decoder as? JsonDecoder
                ?: throw SerializationException("TorrentDataSerializer can be used only with Json format")

        return when (val element = jsonDecoder.decodeJsonElement()) {
            JsonNull -> null
            is JsonArray -> element.map { jsonDecoder.json.decodeFromJsonElement(TorrentDto.serializer(), it) }
            is JsonObject -> listOf(jsonDecoder.json.decodeFromJsonElement(TorrentDto.serializer(), element))
            else -> throw SerializationException("Expected data to be object, array, or null")
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: List<TorrentDto>?,
    ) {
        val jsonEncoder =
            encoder as? JsonEncoder
                ?: throw SerializationException("TorrentDataSerializer can be used only with Json format")

        val element =
            when {
                value == null -> JsonNull
                else -> JsonArray(value.map { jsonEncoder.json.encodeToJsonElement(TorrentDto.serializer(), it) })
            }

        jsonEncoder.encodeJsonElement(element)
    }
}
