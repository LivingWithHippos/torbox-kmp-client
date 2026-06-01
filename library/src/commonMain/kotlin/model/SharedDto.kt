package com.github.livingwithhippos.torbox.kmp.client.model

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
data class CacheAvailabilityDto(
    val name: String,
    val size: Long,
    val hash: String,
)

@Serializable
data class CacheAvailabilityRequestDto(
    val format: String? = null,
    val hashes: List<String>,
)
