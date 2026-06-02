package com.github.livingwithhippos.torbox.api

import com.github.livingwithhippos.torbox.model.ApiResponseDto
import com.github.livingwithhippos.torbox.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.model.NotificationDto
import com.github.livingwithhippos.torbox.network.ApiResult

interface NotificationsApi {
    /**
     * Gets your notifications in an RSS Feed which allows you to use them with RSS Feed readers or notification services that can take RSS Feeds and listen to updates.
     * As soon as a notification goes to your account, it will be added to your feed.
     *
     * [online documentation](https://api-docs.torbox.app/#aca5c446-4db7-456d-b715-cc567ef094fb)
     *
     * XML is returned as an unparsed string
     */
    suspend fun rssFeed(token: String): ApiResult<String>

    /**
     * Gets your notifications in a JSON object that is easily parsable compared to the RSS Feed.
     * Gives all the same data as the RSS Feed.
     *
     * [online documentation](https://api-docs.torbox.app/#5ba21bea-2f21-466d-90cb-29337e772eca)
     */
    suspend fun jsonFeed(): ApiResult<ApiResponseListDto<NotificationDto>>

    /**
     *  Marks all of your notifications as read and deletes them permanently.
     *
     *  If [notificationId] (which can be found by getting your notification feed) is specified, marks a single notification as read and permanently deletes it from your notifications.
     *
     * [online documentation for clear all](https://api-docs.torbox.app/#8260688c-f430-42f4-988c-b02d7fd79dc5)
     *
     * [online documentation for clear single](https://api-docs.torbox.app/#699d4381-3a3b-4979-9ec9-99cfb23ff038)
     *
     * Merged version of two APIs
     */
    suspend fun clear(notificationId: Int?): ApiResult<ApiResponseDto<String>>

    /**
     * Sends a test notification to all enabled notification types.
     * This can be useful for validating setups. No need for any body in this request.
     *
     * [online documentation](https://api-docs.torbox.app/#03709e2d-4c17-49cb-bcac-bf9c1c99c536)
     */
    suspend fun sendTest(): ApiResult<ApiResponseDto<String>>
}
