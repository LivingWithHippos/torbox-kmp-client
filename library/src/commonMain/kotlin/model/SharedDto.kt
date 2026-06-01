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

/**
 * base response returned by the api. Data is always present and may be null, especially in errors.
 * The only precaution is to check for success = true when this is the return type
 * since even errors returns this
 *
 * @param success false if there was an error
 * @param error if success is false this could be an error code, such as "DATABASE_ERROR", see [detail] for more info
 * @param detail a string detailing the operation result
 * @param data an optional string which may contain the valid response, if the API has a return value
 */
@Serializable
data class GenericResponseDto(
    override val success: Boolean,
    val error: String?,
    val detail: String,
    val data: String? = null,
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
