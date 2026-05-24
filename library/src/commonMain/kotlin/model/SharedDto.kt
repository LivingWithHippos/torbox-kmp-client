package com.github.livingwithhippos.torbox.kmp.client.model

import kotlinx.serialization.Serializable

@Serializable
data class GenericResponseDto(
    val success: Boolean,
    val error: String?,
    val detail: String,
)

@Serializable
data class DownloadResponseDto(
    val success: Boolean,
    val error: String?,
    val detail: String,
    val data: String?,
)
