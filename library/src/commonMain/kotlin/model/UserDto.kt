package com.github.livingwithhippos.torbox.kmp.client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshApiTokenRequestDto(
    @SerialName("session_token")
    val sessionToken: String,
)
