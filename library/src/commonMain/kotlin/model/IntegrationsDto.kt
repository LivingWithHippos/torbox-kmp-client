package com.github.livingwithhippos.torbox.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JobDto(
    val id: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("auth_id") val authId: String,
    val hash: String,
    val type: String,
    val integration: String,
    @SerialName("file_id") val fileId: Int,
    val zip: Boolean,
    val progress: Int,
    val detail: String,
    @SerialName("download_url") val downloadUrl: String? = null,
    val status: String,
    @SerialName("file_name") val fileName: String,
    @SerialName("task_id") val taskId: String,
)
