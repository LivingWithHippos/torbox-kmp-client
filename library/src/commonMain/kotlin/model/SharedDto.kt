package com.github.livingwithhippos.torbox.kmp.client.model

import kotlinx.serialization.Serializable

interface ApiSuccessResponse {
    val success: Boolean
}

/**
 * base response returned by the api. Data is always present and may be null, especially in errors.
 * The only precaution is to check for success = true when this is the return type
 * since even errors returns this
 */
@Serializable
data class GenericResponseDto(
    override val success: Boolean,
    val error: String?,
    val detail: String,
    val data: String? = null,
) : ApiSuccessResponse

@Serializable
data class CacheAvailabilityObjectResponseDto(
    override val success: Boolean,
    val error: String?,
    val detail: String,
    val data: Map<String, CacheAvailabilityDto>?,
) : ApiSuccessResponse

@Serializable
data class CacheAvailabilityListResponseDto(
    override val success: Boolean,
    val error: String?,
    val detail: String,
    val data: List<CacheAvailabilityDto>?,
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
