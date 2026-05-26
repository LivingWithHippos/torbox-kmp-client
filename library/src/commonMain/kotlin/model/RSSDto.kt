package com.github.livingwithhippos.torbox.kmp.client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddRssRequestDto(
    val url: String,
    val name: String,
    @SerialName("do_regex") val doRegex: String? = null,
    @SerialName("dont_regex") val dontRegex: String? = null,
    @SerialName("dont_older_than") val dontOlderThan: Int? = null,
    @SerialName("scan_interval") val scanInterval: Int? = null,
    @SerialName("pass_check") val passCheck: Boolean? = null,
    @SerialName("rss_type") val rssType: String? = null,
    @SerialName("torrent_seeding") val torrentSeeding: Int? = null,
)

@Serializable
data class ControlRssRequestDto(
    val operation: String,
    @SerialName("rss_feed_id") val rssFeedId: Int,
)

// we could unify this and AddRssRequestDto
@Serializable
data class EditRssRequestDto(
    @SerialName("rss_feed_id") val rssFeedId: Int,
    val name: String? = null,
    @SerialName("do_regex") val doRegex: String? = null,
    @SerialName("dont_regex") val dontRegex: String? = null,
    @SerialName("dont_older_than") val dontOlderThan: Int? = null,
    @SerialName("scan_interval") val scanInterval: Int? = null,
    @SerialName("pass_check") val passCheck: Boolean? = null,
    @SerialName("rss_type") val rssType: String? = null,
    @SerialName("torrent_seeding") val torrentSeeding: Int? = null,
)

@Serializable
data class UserRssFeedsResponseDto(
    override val success: Boolean,
    val error: String?,
    val detail: String,
    val data: List<RssFeedDto>?,
) : ApiSuccessResponse

@Serializable
data class RssFeedDto(
    val id: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("last_checked") val lastChecked: String? = null,
    @SerialName("auth_id") val authId: String,
    val name: String,
    val url: String,
    val source: String? = null,
    @SerialName("source_name") val sourceName: String? = null,
    @SerialName("do_regex") val doRegex: String? = null,
    @SerialName("dont_regex") val dontRegex: String? = null,
    val status: String,
    @SerialName("scan_interval") val scanInterval: Int,
    @SerialName("dont_older_than") val dontOlderThan: Int,
    @SerialName("status_message") val statusMessage: String? = null,
    val type: String,
    @SerialName("torrent_seeding") val torrentSeeding: Int,
    val state: String,
)

@Serializable
data class UserRssFeedItemsResponseDto(
    override val success: Boolean,
    val error: String?,
    val detail: String,
    val data: List<RssFeedItemDto>?,
) : ApiSuccessResponse

@Serializable
data class RssFeedItemDto(
    val id: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("rss_name") val rssName: String,
    @SerialName("rss_url") val rssUrl: String,
    val ignored: Boolean,
    val downloaded: Boolean,
    val link: String,
    val name: String,
    val status: String,
    @SerialName("status_message") val statusMessage: String? = null,
    @SerialName("seed_torrents") val seedTorrents: Int,
    val type: String,
)

