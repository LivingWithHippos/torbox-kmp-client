package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.AddRssRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
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
}
