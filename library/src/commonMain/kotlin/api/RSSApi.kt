package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.RssFeedDto
import com.github.livingwithhippos.torbox.kmp.client.model.RssFeedItemDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface RSSApi {
    /**
     * Adds an RSS feed to the authenticated user's account
     *
     * [online documentation](https://api-docs.torbox.app/#308901ab-cb21-424c-806d-f243d01f16dd)
     */
    suspend fun addRss(
        url: String,
        name: String,
        doRegex: String? = null,
        dontRegex: String? = null,
        dontOlderThan: Int? = null,
        scanInterval: Int? = null,
        passCheck: Boolean? = null,
        rssType: String? = null,
        torrentSeeding: Int? = null,
    ): ApiResult<GenericResponseDto>

    /**
     * Controls an RSS Feed. By sending the RSS feed's ID and the type of operation you want to perform,
     * it will perform said action on the RSS feed checker.
     *
     * [online documentation](https://api-docs.torbox.app/#f89d49e9-4682-493d-a26b-591c8793ad13)
     */
    suspend fun controlRss(
        operation: String,
        rssFeedId: Int,
    ): ApiResult<GenericResponseDto>

    /**
     * Edits an existing RSS feed
     *
     * [online documentation](https://api-docs.torbox.app/#e69680e6-f21b-4613-ab70-80598b1ab838)
     */
    suspend fun editRss(
        rssFeedId: Int,
        name: String? = null,
        doRegex: String? = null,
        dontRegex: String? = null,
        dontOlderThan: Int? = null,
        scanInterval: Int? = null,
        passCheck: Boolean? = null,
        rssType: String? = null,
        torrentSeeding: Int? = null,
    ): ApiResult<GenericResponseDto>

    /**
     * Gets all RSS feeds on the authenticated user's account, or one specific feed when [id] is provided.
     *
     * [online documentation](https://api-docs.torbox.app/#1624cad4-28fd-4bb0-934d-2246d1810cbc)
     */
    suspend fun getUserRssFeeds(id: Int? = null): ApiResult<ApiResponseListDto<RssFeedDto>>

    /**
     * Gets the first 10,000 RSS feed items associated with an RSS feed.
     * RSS feed items are scraped items from the RSS feed.
     * They contain a direct download link as well as the content name, and additional information.
     *
     * [online documentation](https://api-docs.torbox.app/#5e2946d4-bfc8-4d0c-94c1-399c35b7cefa)
     */
    suspend fun getRssFeeds(id: Int): ApiResult<ApiResponseListDto<RssFeedItemDto>>
}
