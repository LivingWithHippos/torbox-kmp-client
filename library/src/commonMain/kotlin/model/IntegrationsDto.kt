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
data class JobsResponseDto(
    override val success: Boolean,
    val error: String?,
    val detail: String,
    @Serializable(with = JobSerializer::class)
    val data: List<JobDto>?,
) : ApiSuccessResponse

@Serializable
data class JobDto(
    val id: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("auth_id") val authId: String,
    val hash: String,
    val type: String,
    val integration: String,
    @SerialName("file_id") val fileId: Int,
    val zip: Boolean,
    val progress: Int,
    val detail: String,
    @SerialName("download_url") val downloadUrl: String? = null,
    val status: String,
    @SerialName("file_name") val fileName: String,
    @SerialName("task_id") val taskId: String,
)

/**
 * this custom serializer is used to avoid creating two data classes whose only difference is returning a list of jobs
 * or a single job in the data field. We return a list with the job instead of the single job.
 */
@Serializable
object JobSerializer : KSerializer<List<JobDto>?> {
    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

    override fun deserialize(decoder: Decoder): List<JobDto>? {
        val jsonDecoder =
            decoder as? JsonDecoder
                ?: throw SerializationException("JobSerializer can be used only with Json format")

        return when (val element = jsonDecoder.decodeJsonElement()) {
            JsonNull -> null
            is JsonArray -> element.map { jsonDecoder.json.decodeFromJsonElement(JobDto.serializer(), it) }
            is JsonObject -> listOf(jsonDecoder.json.decodeFromJsonElement(JobDto.serializer(), element))
            else -> throw SerializationException("Expected data to be object, array, or null")
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: List<JobDto>?,
    ) {
        val jsonEncoder =
            encoder as? JsonEncoder
                ?: throw SerializationException("JobSerializer can be used only with Json format")

        val element =
            when {
                value == null -> JsonNull
                else -> JsonArray(value.map { jsonEncoder.json.encodeToJsonElement(JobDto.serializer(), it) })
            }

        jsonEncoder.encodeJsonElement(element)
    }
}
