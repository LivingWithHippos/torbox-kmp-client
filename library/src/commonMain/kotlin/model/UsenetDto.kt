package com.github.livingwithhippos.torbox.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateUsenetDto(
    val hash: String,
    @SerialName("usenetdownload_id") val usenetId: Long,
    @SerialName("auth_id") val authId: String,
)

@Serializable
data class UsenetControlRequestDto(
    @SerialName("usenet_id")
    val usenetId: Int? = null,
    val operation: String,
    val all: Boolean? = null,
)

@Serializable
data class EditUsenetRequestDto(
    @SerialName("usenet_download_id")
    val usenetId: Int,
    val name: String? = null,
    val tags: List<String>? = null,
    @SerialName("alternative_hashes")
    val alternativeHashes: List<String>? = null,
)

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
    val server: Int?,
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
data class WebControlRequestDto(
    @SerialName("webdl_id")
    val webId: Int? = null,
    val operation: String,
    val all: Boolean? = null,
)
