package com.github.livingwithhippos.torbox.kmp.client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateWebDownloadResponseDto(
    val success: Boolean,
    val error: String?,
    val detail: String,
    val data: CreateWebDownloadDto?,
)

@Serializable
data class CreateWebDownloadDto(
    val hash: String,
    @SerialName("webdownload_id") val webDownloadId: Int,
    @SerialName("auth_id") val authId: String,
    @SerialName("jdownloader_id") val jdownloaderId: String?,
    @SerialName("link_list") val linkList: List<String>?,  // missing if already queued
)
