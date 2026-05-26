package com.github.livingwithhippos.torbox.kmp.client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: Int,
    @SerialName("created_at") val createdAt: String,
    val title: String,
    val message: String,
    @SerialName("auth_id") val authId: String,
)
