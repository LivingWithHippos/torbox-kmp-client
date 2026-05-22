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


@Serializable
data class WebDownloadListResponseDto(
    val success: Boolean,
    val error: String?,
    val detail: String,
    // todo: add support for id parameter which has just val data: WebDownloadDto? as difference
    val data: List<WebDownloadDto>?,
)

@Serializable
data class WebDownloadDto(
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
    val files: List<WebDownloadFileDto>,
    val active: Boolean,
    val cached: Boolean,
    @SerialName("download_present") val downloadPresent: Boolean,
    @SerialName("download_finished") val downloadFinished: Boolean,
    @SerialName("expires_at") val expiresAt: String?,
    val error: String?,
    @SerialName("cached_at") val cachedAt: String?,
    val server: Int,
    @SerialName("alternative_hashes") val alternativeHashes: List<String>,
    val tags: List<String>,
)

@Serializable
data class WebDownloadFileDto(
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