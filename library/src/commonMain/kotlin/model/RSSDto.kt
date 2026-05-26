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
