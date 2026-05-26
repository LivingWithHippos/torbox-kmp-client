package com.github.livingwithhippos.torbox.kmp.client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatsResponseDto(
    override val success: Boolean,
    val error: Boolean?,
    val detail: String,
    val data: StatsDto?,
) : ApiSuccessResponse

@Serializable
data class StatsDto(
    @SerialName("total_users") val totalUsers: Long,
    @SerialName("total_servers") val totalServers: Long,
)
