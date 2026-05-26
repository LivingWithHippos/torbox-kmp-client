package com.github.livingwithhippos.torbox.kmp.client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatsDto(
    @SerialName("total_users") val totalUsers: Long,
    @SerialName("total_servers") val totalServers: Long,
)

@Serializable
data class ChangelogListResponseDto(
    override val success: Boolean,
    val error: String?,
    val detail: String,
    val data: List<ChangelogEntryDto>?,
) : ApiSuccessResponse

@Serializable
data class ChangelogEntryDto(
    val id: String,
    val name: String,
    val html: String,
    val markdown: String,
    val link: String,
    @SerialName("created_at") val createdAt: String,
)

@Serializable
data class SpeedtestFilesResponseDto(
    override val success: Boolean,
    val error: String?,
    val detail: String,
    val data: List<SpeedtestFileDto>?,
) : ApiSuccessResponse

@Serializable
data class SpeedtestFileDto(
    val region: String,
    val name: String,
    val domain: String,
    val path: String,
    val url: String,
    val closest: Boolean,
    val coordinates: SpeedtestCoordinatesDto,
)

@Serializable
data class SpeedtestCoordinatesDto(
    val lat: Double,
    val lng: Double,
)
