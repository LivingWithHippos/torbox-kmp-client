package com.github.livingwithhippos.torbox.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateWebDownloadDto(
    val hash: String,
    @SerialName("webdownload_id") val webDownloadId: Int,
    @SerialName("auth_id") val authId: String,
    @SerialName("jdownloader_id") val jdownloaderId: String?,
    @SerialName("link_list") val linkList: List<String>?, // missing if already queued
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
    val files: List<WebDownloadFileDto> = emptyList(),
    val active: Boolean,
    val cached: Boolean,
    @SerialName("download_present") val downloadPresent: Boolean,
    @SerialName("download_finished") val downloadFinished: Boolean,
    @SerialName("expires_at") val expiresAt: String?,
    val error: String?,
    @SerialName("cached_at") val cachedAt: String?,
    val server: Int?,
    @SerialName("alternative_hashes") val alternativeHashes: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    @SerialName("airlocked")
    val airLocked: Boolean? = null,
)

@Serializable
data class WebDownloadFileDto(
    val id: Int,
    val md5: String?,
    val hash: String? = null,
    val name: String,
    val size: Long,
    val zipped: Boolean = false,
    @SerialName("s3_path") val s3Path: String,
    val infected: Boolean = false,
    val mimetype: String,
    @SerialName("short_name") val shortName: String,
    @SerialName("absolute_path") val absolutePath: String? = null,
    @SerialName("opensubtitles_hash") val opensubtitlesHash: String?,
)

@Serializable
data class HosterDto(
    val id: Int,
    val name: String,
    val domains: List<String>,
    val url: String,
    val icon: String,
    val status: Boolean,
    val type: String,
    val note: String?,
    val nsfw: Boolean = false,
    @SerialName("daily_link_limit") val dailyLinkLimit: Int,
    @SerialName("daily_link_used") val dailyLinkUsed: Int,
    @SerialName("daily_bandwidth_limit") val dailyBandwidthLimit: Long,
    @SerialName("daily_bandwidth_used") val dailyBandwidthUsed: Long,
    @SerialName("per_link_size_limit") val perLinkSizeLimit: Long? = null,
    val regex: String? = null,
)

@Serializable
data class EditWebRequestDto(
    @SerialName("webdl_id")
    val webId: Int,
    val name: String? = null,
    val tags: List<String>? = null,
    @SerialName("alternative_hashes")
    val alternativeHashes: List<String>? = null,
)
