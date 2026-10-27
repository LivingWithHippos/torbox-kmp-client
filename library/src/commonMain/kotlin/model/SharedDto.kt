package com.github.livingwithhippos.torbox.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface ApiSuccessResponse {
    val success: Boolean
}

@Serializable
data class ApiResponseDto<T>(
    override val success: Boolean,
    val error: String?,
    val detail: String,
    val data: T? = null,
) : ApiSuccessResponse

@Serializable
data class ApiResponseListDto<T>(
    override val success: Boolean,
    val error: String?,
    val detail: String,
    val data: List<T>? = null,
) : ApiSuccessResponse

@Serializable
data class ApiResponseMapDto<T>(
    override val success: Boolean,
    val error: String?,
    val detail: String,
    val data: Map<String, T>? = null,
) : ApiSuccessResponse

@Serializable
data class CacheDto(
    val name: String,
    val size: Long,
    val hash: String,
    val files: List<CacheFileDto>?,
)

@Serializable
data class CacheFileDto(
    val id: Int,
    val name: String,
    val size: Long,
    @SerialName("opensubtitles_hash") val opensubtitlesHash: String?,
    @SerialName("short_name") val shortName: String?,
    val mimetype: String?,
)

@Serializable
data class CacheRequestDto(
    val hashes: List<String>,
)
