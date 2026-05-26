package com.github.livingwithhippos.torbox.kmp.client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshApiTokenRequestDto(
    @SerialName("session_token")
    val sessionToken: String,
)

@Serializable
data class UserDataResponseDto(
    override val success: Boolean,
    val error: String?,
    val detail: String,
    val data: UserDataDto?,
) : ApiSuccessResponse

@Serializable
data class UserDataDto(
    val id: Int,
    @SerialName("auth_id") val authId: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    val plan: Int,
    @SerialName("total_downloaded") val totalDownloaded: Int,
    val customer: String,
    @SerialName("is_subscribed") val isSubscribed: Boolean,
    @SerialName("premium_expires_at") val premiumExpiresAt: String?,
    @SerialName("cooldown_until") val cooldownUntil: String?,
    val email: String,
    @SerialName("user_referral") val userReferral: String,
    @SerialName("base_email") val baseEmail: String,
    @SerialName("total_bytes_downloaded") val totalBytesDownloaded: Long,
    @SerialName("total_bytes_uploaded") val totalBytesUploaded: Long,
    @SerialName("torrents_downloaded") val torrentsDownloaded: Int,
    @SerialName("web_downloads_downloaded") val webDownloadsDownloaded: Int,
    @SerialName("usenet_downloads_downloaded") val usenetDownloadsDownloaded: Int,
    @SerialName("additional_concurrent_slots") val additionalConcurrentSlots: Int,
    @SerialName("long_term_seeding") val longTermSeeding: Boolean,
    @SerialName("long_term_storage") val longTermStorage: Boolean,
    @SerialName("is_vendor") val isVendor: Boolean,
    @SerialName("vendor_id") val vendorId: Int?,
    @SerialName("purchases_referred") val purchasesReferred: Int,
)
