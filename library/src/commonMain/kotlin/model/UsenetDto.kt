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
data class UsenetDownloadListResponseDto(
    override val success: Boolean,
    val error: String?,
    val detail: String,
    @Serializable(with = UsenetDownloadDataSerializer::class)
    val data: List<UsenetDownloadDto>?,
) : ApiSuccessResponse

@Serializable
data class UsenetDownloadDto(
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
    val files: List<UsenetDownloadFileDto> = emptyList(),
    val active: Boolean,
    val cached: Boolean,
    @SerialName("download_present") val downloadPresent: Boolean,
    @SerialName("download_finished") val downloadFinished: Boolean,
    @SerialName("expires_at") val expiresAt: String?,
    val server: Int,
    @SerialName("cached_at") val cachedAt: String?,
    @SerialName("alternative_hashes") val alternativeHashes: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
)

@Serializable
data class UsenetDownloadFileDto(
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
object UsenetDownloadDataSerializer : KSerializer<List<UsenetDownloadDto>?> {
    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

    override fun deserialize(decoder: Decoder): List<UsenetDownloadDto>? {
        val jsonDecoder =
            decoder as? JsonDecoder
                ?: throw SerializationException("UsenetDownloadDataSerializer can be used only with Json format")

        return when (val element = jsonDecoder.decodeJsonElement()) {
            JsonNull -> null
            is JsonArray -> element.map { jsonDecoder.json.decodeFromJsonElement(UsenetDownloadDto.serializer(), it) }
            is JsonObject -> listOf(jsonDecoder.json.decodeFromJsonElement(UsenetDownloadDto.serializer(), element))
            else -> throw SerializationException("Expected data to be object, array, or null")
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: List<UsenetDownloadDto>?,
    ) {
        val jsonEncoder =
            encoder as? JsonEncoder
                ?: throw SerializationException("UsenetDownloadDataSerializer can be used only with Json format")

        val element =
            when {
                value == null -> JsonNull
                else -> JsonArray(value.map { jsonEncoder.json.encodeToJsonElement(UsenetDownloadDto.serializer(), it) })
            }

        jsonEncoder.encodeJsonElement(element)
    }
}

@Serializable
data class WebControlRequestDto(
    @SerialName("webdl_id")
    val webId: Int? = null,
    val operation: String,
    val all: Boolean? = null,
)
