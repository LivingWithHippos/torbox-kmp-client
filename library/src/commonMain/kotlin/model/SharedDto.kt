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
) : ApiSuccessResponse

@Serializable
data class DownloadResponseDto(
    override val success: Boolean,
    val error: String?,
    val detail: String,
    val data: String?,
) : ApiSuccessResponse
