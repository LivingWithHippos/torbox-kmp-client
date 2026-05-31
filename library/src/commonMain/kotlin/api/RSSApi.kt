package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.RssFeedDto
import com.github.livingwithhippos.torbox.kmp.client.model.RssFeedItemDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface RSSApi {
    /**
     * Allows adding an RSS feed to your account.
     * This API gives you a lot of customization options, but is best used with the UI on the website.
     * This endpoint only works for Pro users (plan: 2).
     *
     * [online documentation](https://api-docs.torbox.app/#308901ab-cb21-424c-806d-f243d01f16dd)
     */
    suspend fun add(
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
     * Operations are either:
     *
     *     - Update: forces an update on the rss feed
     *     - Delete: deletes the rss feed from your account permanently. This also deletes all associated RSS feed items. This does not remove queued items spawned from the RSS feed.
     *     - Pause: pauses checking the rss feed on the scan interval
     *     - Resume: resumes a paused rss feed
     *
     * [online documentation](https://api-docs.torbox.app/#f89d49e9-4682-493d-a26b-591c8793ad13)
     */
    suspend fun controlRss(
        operation: String,
        rssFeedId: Int,
    ): ApiResult<GenericResponseDto>

    /**
     * Allows you to edit the RSS feed based on the RSS feed ID passed.
     * Allows you to change everything about an RSS feed, except for the URL.
     *
     * When updating the [rssType] or the [torrentSeeding] options, it will update all the RSS items associated with the RSS feed,
     * including all previous items.
     *
     * [online documentation](https://api-docs.torbox.app/#e69680e6-f21b-4613-ab70-80598b1ab838)
     */
    suspend fun edit(
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
     * Gets all of a user's RSS feeds located on their account.
     * Can only be accessed on a Pro (plan: 2) account.
     * Also allows you to get a specific by passing an [id] in the parameters.
     *
     * **Type Key**
     *
     * The "type" key in the response is used for showing what type of downloads are spawned from this RSS feed. This affects how the item is downloaded, as well as what is scraped, so make sure it is correct.
     *
     *     "torrent" uses .torrent files and magnet links and adds as torrent when downloaded.
     *     "usenet" uses .nzb files and nzb links and adds as a usenet download when downloaded.
     *     "webdl" uses generic links and adds as a web download when downloaded.
     *
     * **Status Key**
     *
     * The "status" key tells the user selected status of the RSS feed. These statuses are not the same as the status key for the RSS items associated with the RSS feed. Each feed also includes a "status_message" key which gives a user friendly message about anything about the RSS feed, such as errors and explainations.
     *
     *     "active" the RSS feed is set to be scraped.
     *
     *     "paused" the RSS feed will not be scraped as it is paused by the user.
     *
     *     "error" the RSS feed had an error while scraping, and cannot continue.
     *
     * **State Key**
     *
     * The "state" key tells you exactly in real time, what the RSS feed is doing. This is useful for realtime status updates.
     *
     *     "idle" the RSS feed is idle, and is currently not doing anything in the current moment.
     *     "scraping" the RSS feed is currently scraping new RSS items.
     *     "downloading" the RSS feed is currently downloading the newly scraped RSS items.
     *
     * **Torrent Seeding Key**
     *
     * The "torrent_seeding" key is returned with every response, regardless of the type of the RSS feed. This controls what seeding setting the RSS feed is using when it adds new torrents. These are the same options as when adding a torrent using the /torrent/createtorrent endpoint.
     *
     *     Tells TorBox your preference for seeding this torrent. 1 is auto. 2 is seed. 3 is don't seed. Optional. Default is 1, or whatever the user has in their settings. Overwrites option in settings.
     *
     *     1 auto, the torrent will seed based on what the user has in their settings.
     *     2 always, TorBox will be instructed to seed this torrent regardless of any other settings.
     *     3 never, TorBox will not seed this torrent.
     *
     * [online documentation](https://api-docs.torbox.app/#1624cad4-28fd-4bb0-934d-2246d1810cbc)
     */
    suspend fun userFeeds(id: Int? = null): ApiResult<ApiResponseListDto<RssFeedDto>>

    /**
     * Gets the first 10,000 RSS feed items associated with an RSS feed.
     * RSS feed items are scraped items from the RSS feed.
     * They contain a direct download link as well as the content name, and additional information.
     *
     * [online documentation](https://api-docs.torbox.app/#5e2946d4-bfc8-4d0c-94c1-399c35b7cefa)
     */
    suspend fun feeds(id: Int): ApiResult<ApiResponseListDto<RssFeedItemDto>>
}
