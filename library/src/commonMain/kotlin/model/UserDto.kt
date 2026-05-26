package com.github.livingwithhippos.torbox.kmp.client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshApiTokenRequestDto(
    @SerialName("session_token") val sessionToken: String,
)

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

@Serializable
data class DeviceCodeAuthorizationStartDto(
    @SerialName("device_code") val deviceCode: String,
    val interval: Int,
    @SerialName("expires_at") val expiresAt: String,
    @SerialName("verification_url") val verificationUrl: String,
    @SerialName("friendly_verification_url") val friendlyVerificationUrl: String,
    val code: String,
)

@Serializable
data class DeviceCodeTokenRequestDto(
    @SerialName("device_code") val deviceCode: String,
)

@Serializable
data class DeviceCodeTokenDataDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
)

@Serializable
data class ReferralDataDto(
    @SerialName("referred_accounts") val referredAccounts: Int,
    @SerialName("referral_code") val referralCode: String,
    @SerialName("purchases_referred") val purchasesReferred: Int,
)

@Serializable
data class SubscriptionDto(
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("subscription_code") val subscriptionCode: String,
    @SerialName("email_token") val emailToken: String,
    @SerialName("auth_id") val authId: String,
    @SerialName("plan_code") val planCode: String,
    val status: String,
    val gateway: String,
    @SerialName("plan_name") val planName: String,
)

@Serializable
data class TransactionDto(
    @SerialName("at") val at: String,
    val type: String,
    val amount: Long,
    @SerialName("transaction_id") val transactionId: String,
)

@Serializable
data class UserSettingsRequestDto(
    @SerialName("email_notifications") val emailNotifications: Boolean?,
    @SerialName("web_notifications") val webNotifications: Boolean?,
    @SerialName("mobile_notifications") val mobileNotifications: Boolean?,
    @SerialName("rss_notifications") val rssNotifications: Boolean?,
    @SerialName("download_speed_in_tab") val downloadSpeedInTab: Boolean?,
    @SerialName("show_tracker_in_torrents") val showTrackerInTorrents: Boolean?,
    @SerialName("stremio_quality") val stremioQuality: List<Int>?,
    @SerialName("stremio_resolution") val stremioResolution: List<Int>?,
    @SerialName("stremio_language") val stremioLanguage: List<Int>?,
    @SerialName("stremio_cache") val stremioCache: List<Int>?,
    @SerialName("stremio_size_lower") val stremioSizeLower: Long?,
    @SerialName("google_drive_folder_id") val googleDriveFolderId: String?,
    @SerialName("onedrive_save_path") val onedriveSavePath: String?,
    @SerialName("discord_id") val discordId: String?,
    @SerialName("discord_notifications") val discordNotifications: Boolean?,
    @SerialName("stremio_allow_adult") val stremioAllowAdult: Boolean?,
    @SerialName("stremio_seed_torrents") val stremioSeedTorrents: Int?,
    @SerialName("seed_torrents") val seedTorrents: Int?,
    @SerialName("allow_zipped") val allowZipped: Boolean?,
    @SerialName("stremio_allow_zipped") val stremioAllowZipped: Boolean?,
    @SerialName("onefichier_folder_id") val onefichierFolderId: String?,
    @SerialName("gofile_folder_id") val gofileFolderId: String?,
    @SerialName("jdownloader_notifications") val jdownloaderNotifications: Boolean?,
    @SerialName("webhook_notifications") val webhookNotifications: Boolean?,
    @SerialName("webhook_url") val webhookUrl: String?,
    @SerialName("telegram_notifications") val telegramNotifications: Boolean?,
    @SerialName("telegram_id") val telegramId: String?,
    @SerialName("mega_email") val megaEmail: String?,
    @SerialName("mega_password") val megaPassword: String?,
    @SerialName("stremio_sort") val stremioSort: String?,
    @SerialName("cdn_selection") val cdnSelection: String?,
    @SerialName("stremio_use_custom_search_engines") val stremioUseCustomSearchEngines: Boolean?,
    @SerialName("stremio_result_sort") val stremioResultSort: String?,
    @SerialName("webdav_use_local_files") val webdavUseLocalFiles: Boolean?,
    @SerialName("stremio_legacy_your_media") val stremioLegacyYourMedia: Boolean?,
    @SerialName("stremio_only_your_media_streams") val stremioOnlyYourMediaStreams: Boolean?,
    @SerialName("stremio_disable_your_media_streams") val stremioDisableYourMediaStreams: Boolean?,
    @SerialName("webdav_use_folder_view") val webdavUseFolderView: Boolean?,
    @SerialName("webdav_flatten") val webdavFlatten: Boolean?,
    @SerialName("stremio_limit_per_resolution_torrent") val stremioLimitPerResolutionTorrent: Int?,
    @SerialName("stremio_limit_per_resolution_usenet") val stremioLimitPerResolutionUsenet: Int?,
    @SerialName("stremio_size_upper") val stremioSizeUpper: Long?,
    @SerialName("stremio_torrent_seeders_cutoff") val stremioTorrentSeedersCutoff: Int?,
    @SerialName("pixeldrain_api_key") val pixeldrainApiKey: String?,
    @SerialName("onefichier_api_key") val onefichierApiKey: String?,
    @SerialName("gofile_api_key") val gofileApiKey: String?,
    @SerialName("patreon_id") val patreonId: String?,
    @SerialName("stremio_wait_for_download_usenet") val stremioWaitForDownloadUsenet: Boolean?,
    @SerialName("stremio_wait_for_download_torrent") val stremioWaitForDownloadTorrent: Boolean?,
    @SerialName("stremio_disable_filtered_note") val stremioDisableFilteredNote: Boolean?,
    @SerialName("stremio_emoji_in_description") val stremioEmojiInDescription: Boolean?,
    @SerialName("dashboard_filter") val dashboardFilter: DashboardFilterDto?,
    @SerialName("web_player_always_transcode") val webPlayerAlwaysTranscode: Boolean?,
    @SerialName("web_player_always_skip_intro") val webPlayerAlwaysSkipIntro: Boolean?,
    @SerialName("web_player_audio_preferred_language") val webPlayerAudioPreferredLanguage: String?,
    @SerialName("web_player_subtitle_preferred_language") val webPlayerSubtitlePreferredLanguage: String?,
    @SerialName("web_player_disable_prestream_selector") val webPlayerDisablePrestreamSelector: Boolean?,
    @SerialName("dashboard_sort") val dashboardSort: String?,
    @SerialName("web_player_disable_next_up_dialogue") val webPlayerDisableNextUpDialogue: Boolean?,
    @SerialName("web_player_enable_scrobbling") val webPlayerEnableScrobbling: Boolean?,
    @SerialName("append_filename_to_links") val appendFilenameToLinks: Boolean?,
)

@Serializable
data class DashboardFilterDto(
    val torrents: Boolean,
    @SerialName("usenet_downloads") val usenetDownloads: Boolean,
    @SerialName("web_downloads") val webDownloads: Boolean,
    val active: Boolean,
    val inactive: Boolean,
    val queued: Boolean,
    val downloading: Boolean,
    val uploading: Boolean,
    val processing: Boolean,
    @SerialName("download_ready") val downloadReady: Boolean,
    val cached: Boolean,
    val uncached: Boolean,
    @SerialName("private") val isPrivate: Boolean,
    val borrowed: Boolean,
)

@Serializable
data class SearchEngineDto(
    val id: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("auth_id") val authId: String,
    val type: String,
    val url: String,
    val apikey: String?,
    val active: Boolean,
    val valid: Boolean,
    @SerialName("download_type") val downloadType: String?,
    val indexers: List<String>?,
)

@Serializable
data class SearchEngineControlRequestDto(
    val operation: String,
    val id: Int? = null,
    val all: Boolean? = null,
)

@Serializable
data class ModifySearchEngineRequestDto(
    val id: Int,
    val type: String? = null,
    val url: String? = null,
    val apikey: String? = null,
    @SerialName("download_type") val downloadType: String? = null,
)

@Serializable
data class AddSearchEngineRequestDto(
    val type: String? = null,
    val url: String,
    val apikey: String? = null,
    @SerialName("download_type") val downloadType: String? = null,
)

@Serializable
data class ModifySearchEngineDto(
    val id: Int,
    val type: String,
    val url: String,
    val apikey: String?,
    val indexers: List<String>?,
    @SerialName("download_type") val downloadType: String?,
)

@Serializable
data class UserStatsDto(
    val general: UserStatsGeneralDto? = null,
    val bandwidth: List<UserBandwidthDto>? = null,
)

@Serializable
data class UserStatsGeneralDto(
    @SerialName("total_downloaded") val totalDownloaded: Long,
    @SerialName("total_uploaded") val totalUploaded: Long,
    val ratio: Double,
    @SerialName("total_items_downloaded") val totalItemsDownloaded: Int,
)

@Serializable
data class UserBandwidthDto(
    val date: String,
    @SerialName("bytes_downloaded") val bytesDownloaded: Long,
)
