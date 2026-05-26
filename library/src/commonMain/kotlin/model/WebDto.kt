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
data class CreateWebDownloadResponseDto(
    override val success: Boolean,
    val error: String?,
    val detail: String,
    val data: CreateWebDownloadDto?,
) : ApiSuccessResponse

@Serializable
data class CreateWebDownloadDto(
    val hash: String,
    @SerialName("webdownload_id") val webDownloadId: Int,
    @SerialName("auth_id") val authId: String,
    @SerialName("jdownloader_id") val jdownloaderId: String?,
    @SerialName("link_list") val linkList: List<String>?, // missing if already queued
)

@Serializable
data class WebDownloadListResponseDto(
    override val success: Boolean,
    val error: String?,
    val detail: String,
    @Serializable(with = WebDownloadDataSerializer::class)
    val data: List<WebDownloadDto>?,
) : ApiSuccessResponse

@Serializable
data class WebDownloadDto(
    val id: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("auth_id") val authId: String,
    val name: String,
    val hash: String,
    @SerialName("download_state") val downloadState: String,
    @SerialName("download_speed") val downloadSpeed: Long,
    @SerialName("original_url") val originalUrl: String,
    val eta: Int,
    val progress: Double,
    val size: Long,
    @SerialName("download_id") val downloadId: String?,
    val files: List<WebDownloadFileDto>,
    val active: Boolean,
    val cached: Boolean,
    @SerialName("download_present") val downloadPresent: Boolean,
    @SerialName("download_finished") val downloadFinished: Boolean,
    @SerialName("expires_at") val expiresAt: String?,
    val error: String?,
    @SerialName("cached_at") val cachedAt: String?,
    val server: Int,
    @SerialName("alternative_hashes") val alternativeHashes: List<String>,
    val tags: List<String>,
)

@Serializable
data class WebDownloadFileDto(
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

/**
 * this custom serializer is used to avoid creating two data classes whose only difference is returning a list of downloads
 * or a single downloads in the data field. We return a list with the download instead of the download object.
 */
@Serializable
object WebDownloadDataSerializer : KSerializer<List<WebDownloadDto>?> {
    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

    override fun deserialize(decoder: Decoder): List<WebDownloadDto>? {
        val jsonDecoder =
            decoder as? JsonDecoder
                ?: throw SerializationException("WebDownloadDataSerializer can be used only with Json format")

        return when (val element = jsonDecoder.decodeJsonElement()) {
            JsonNull -> null
            is JsonArray -> element.map { jsonDecoder.json.decodeFromJsonElement(WebDownloadDto.serializer(), it) }
            is JsonObject -> listOf(jsonDecoder.json.decodeFromJsonElement(WebDownloadDto.serializer(), element))
            else -> throw SerializationException("Expected data to be object, array, or null")
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: List<WebDownloadDto>?,
    ) {
        val jsonEncoder =
            encoder as? JsonEncoder
                ?: throw SerializationException("WebDownloadDataSerializer can be used only with Json format")

        val element =
            when {
                value == null -> JsonNull
                else -> JsonArray(value.map { jsonEncoder.json.encodeToJsonElement(WebDownloadDto.serializer(), it) })
            }

        jsonEncoder.encodeJsonElement(element)
    }
}

@Serializable
data class HostersResponseDto(
    override val success: Boolean,
    val error: String?,
    val detail: String,
    val data: List<HosterDto>?,
) : ApiSuccessResponse

@Serializable
data class HosterDto(
    val id: Int,
    val name: String,
    val domains: List<String>,
    val url: String,
    val icon: String,
    val status: Boolean,
    val type: String,
    val note: String?,
    val nsfw: Boolean,
    @SerialName("daily_link_limit") val dailyLinkLimit: Int,
    @SerialName("daily_link_used") val dailyLinkUsed: Int,
    @SerialName("daily_bandwidth_limit") val dailyBandwidthLimit: Long,
    @SerialName("daily_bandwidth_used") val dailyBandwidthUsed: Long,
    @SerialName("per_link_size_limit") val perLinkSizeLimit: Long,
    val regex: String,
)

@Serializable
data class EditWebRequestDto(
    @SerialName("webdl_id")
    val webId: Int,
    val name: String? = null,
    val tags: List<String>? = null,
    @SerialName("alternative_hashes")
    val alternativeHashes: List<String>? = null,
)
