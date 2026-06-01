package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityDto
import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityObjectResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.CreateWebDownloadDto
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.HosterDto
import com.github.livingwithhippos.torbox.kmp.client.model.WebDownloadDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface WebApi {
    /**
     * Creates a web download under your account.
     * Simply send a link to any file on the internet.
     * Once it has been checked, it will begin downloading assuming your account has available active download slots, and they aren't too large.
     *
     * [online documentation](https://api-docs.torbox.app/#ffb01e39-ebdf-4983-9483-f1907a37a133)
     */
    suspend fun create(
        link: String,
        password: String? = null,
        name: String? = null,
        asQueued: Boolean? = null,
        addOnlyIfCached: Boolean? = null,
    ): ApiResult<ApiResponseDto<CreateWebDownloadDto>>

    /**
     * Controls a web download.
     * By sending the web download's ID and the type of operation you want to perform, it will send that request to the debrid client.
     *
     * Operations are either:
     *
     *     Delete deletes the download from the client and your account permanently
     *
     * [online documentation](https://api-docs.torbox.app/#151c9e58-16ca-4e44-91b0-6192fddc5e32)
     */
    suspend fun controlWebLink(
        webId: Int? = null,
        operation: String,
        all: Boolean? = null,
    ): ApiResult<GenericResponseDto>

    /**
     * Gets the user's web download list.
     * This gives you the needed information to perform other usenet actions.
     * Unlike Torrents, this information is updated on its own every 5 seconds for live web downloads.
     *
     * [online documentation](https://api-docs.torbox.app/#3739daf4-b586-4555-80d2-57e5670ee7ca)
     */
    suspend fun listWeb(
        bypassCache: String? = null,
        offset: Int? = null,
        limit: Int? = null,
    ): ApiResult<ApiResponseListDto<WebDownloadDto>>

    /**
     * Gets a user's web download from its id.
     * This gives you the needed information to perform other usenet actions.
     * Unlike Torrents, this information is updated on its own every 5 seconds for live web downloads.
     *
     * [online documentation](https://api-docs.torbox.app/#3739daf4-b586-4555-80d2-57e5670ee7ca)
     */
    suspend fun getWeb(
        bypassCache: String? = null,
        id: Int,
    ): ApiResult<ApiResponseDto<WebDownloadDto>>

    /**
     * Requests the download link from the server.
     * Because downloads are metered, TorBox cannot afford to allow free access to the links directly.
     * This endpoint opens the link for 3 hours for downloads.
     * Once a download is started, the user has nearly unlimited time to download the file.
     * The 1-hour time limit is simply for starting downloads. This prevents long term link sharing.
     *
     * **Permalinks**
     *
     * Instead of generating many CDN urls by requesting this endpoint, you can instead create a permalink such as: https://api.torbox.app/v1/api/torrents/requestdl?token=APIKEY&torrent_id=NUMBER&file_id=NUMBER&redirect=true and when a user clicks on it, it will automatically redirect them to the CDN link. This saves requests and doesn't abuse the API. Use this method rather than saving CDN links as they are not permanent. To invalidate these permalinks, simply reset your API token or delete the item from your dashboard.
     *
     * [online documentation](https://api-docs.torbox.app/#0e681f46-531f-4c97-9a41-358ee5e14d90)
     */
    suspend fun requestDownloadLink(
        token: String,
        webId: Int,
        fileId: Int? = null,
        zipLink: String? = null,
        userIp: String? = null,
        redirect: Boolean? = null,
        appendName: Boolean? = null,
    ): ApiResult<GenericResponseDto>

    /**
     * Takes in a list of comma separated web hashes and checks if the web download is cached.
     * This endpoint only gets a max of around 100 at a time, due to http limits in queries.
     *
     * Performance is very fast. Less than 1 second per 100.
     * Time is approximately O(log n) time for those interested in taking it to its max.
     * That is without caching as well.
     * This endpoint stores a cache for an hour.
     *
     * To get the hash of a web download, you can [read this document](https://support.torbox.app/en/articles/13681109-technical-getting-hashes-for-searches) for all the strategies.
     *
     * [online documentation](https://api-docs.torbox.app/#6beae110-9977-4f22-bf35-4a521c927ff0)
     *
     * Endpoint duplicated because of different response format in [listWebAvailabilityObject]
     */
    suspend fun listWebAvailability(
        hashes: List<String>,
        listFiles: Boolean? = null,
    ): ApiResult<ApiResponseListDto<CacheAvailabilityDto>>

    /**
     * Takes in a list of comma separated web hashes and checks if the web download is cached.
     * This endpoint only gets a max of around 100 at a time, due to http limits in queries.
     *
     * Performance is very fast. Less than 1 second per 100.
     * Time is approximately O(log n) time for those interested in taking it to its max.
     * That is without caching as well.
     * This endpoint stores a cache for an hour.
     *
     * To get the hash of a web download, you can [read this document](https://support.torbox.app/en/articles/13681109-technical-getting-hashes-for-searches) for all the strategies.
     *
     * [online documentation](https://api-docs.torbox.app/#6beae110-9977-4f22-bf35-4a521c927ff0)
     *
     * Endpoint duplicated because of different response format in [listWebAvailability]
     */
    suspend fun listWebAvailabilityObject(
        hashes: List<String>,
        listFiles: Boolean? = null,
    ): ApiResult<CacheAvailabilityObjectResponseDto>

    /**
     * Takes in a list of comma separated web hashes and checks if the web download is cached.
     * With the POST endpoint you can submit a nearly unlimited hashes compared to the GET endpoint which is limited by url parameters.
     *
     * Performance is very fast. Less than 1 second per 100.
     * Time is approximately O(log n) time for those interested in taking it to its max.
     * That is without caching as well.
     * This endpoint stores a cache for an hour.
     *
     * [online documentation](https://api-docs.torbox.app/#f31c99fa-07f3-4cca-a00d-c12e2a09213f)
     *
     * Endpoint duplicated because of different response format in [listWebAvailabilityBatch]
     */
    suspend fun listWebAvailabilityBatchObject(hashes: List<String>): ApiResult<CacheAvailabilityObjectResponseDto>

    /**
     * Takes in a list of comma separated web hashes and checks if the web download is cached.
     * With the POST endpoint you can submit a nearly unlimited hashes compared to the GET endpoint which is limited by url parameters.
     *
     * Performance is very fast. Less than 1 second per 100.
     * Time is approximately O(log n) time for those interested in taking it to its max.
     * That is without caching as well.
     * This endpoint stores a cache for an hour.
     *
     * [online documentation](https://api-docs.torbox.app/#f31c99fa-07f3-4cca-a00d-c12e2a09213f)
     *
     * Endpoint duplicated because of different response format in [listWebAvailabilityBatchObject]
     */
    suspend fun listWebAvailabilityBatch(hashes: List<String>): ApiResult<ApiResponseListDto<CacheAvailabilityDto>>

    /**
     * A dynamic list of hosters that TorBox is capable of downloading through its paid service.
     *
     *     Name - a clean name for display use, the well known name of the service, should be recognizable to users.
     *
     *     Domains - an array of known domains that the hoster uses. While each may serve a different purpose it is still included.
     *
     *     URL - the main url of the service. This should take you to the home page or a service page of the hoster.
     *
     *     Icon - a square image, usually a favicon or logo, that represents the service, should be recognizable as the hoster's icon.
     *
     *     Status - whether this hoster can be used on TorBox or not at the current time. It is usually a good idea to check this value before submitting a download to TorBox's servers for download.
     *
     *     Type - values are either "hoster" or "stream". Both do the same thing, but is good to differentiate services used for different things.
     *
     *     Note - a string value (or null) that may give helpful information about the current status or state of a hoster. This can and should be shown to end users.
     *
     *     Daily Link Limit - the number of downloads a user can use per day. As a user submits links, once they hit this number, the API will deny them from adding anymore of this type of link. A zero value means that it is unlimited.
     *
     *     Daily Link Used - the number of downloads a user has already used. Usually zero unless you send authentication to the endpoint. This will return accurate values.
     *
     *     Daily Bandwidth Limit - the value in bytes that a user is allowed to download from this hoster. A zero value means that it is unlimited. It is recommended to use the Daily Link Limit instead.
     *
     *     Daily Bandwidth Used - the value in bytes that a user has already used to download from this hoster. Usually zero unless you send authentication to the endpoint. This will return accurate values.
     *
     * **Authorization**
     *
     * Optional authorization. Authorization is not required in this endpoint unless you want to get the user's live data. Requires an API key using the Authorization Bearer Header to get the live and accurate data for Daily Link Used and Daily Bandwidth Used.
     *
     * [online documentation](https://api-docs.torbox.app/#0a17f8b0-87a1-459c-b2d9-32d34b00c2ae)
     */
    suspend fun hosters(auth: Boolean = false): ApiResult<ApiResponseListDto<HosterDto>>

    /**
     * Updates a download item based on the item's ID.
     * Sending a PUT request with valid items overwrites the previous items entirely, so use this endpoint with caution. Please make sure to read the restrictions below as they are important.
     *
     * **Restrictions**
     *
     *     Item must be cached to update.
     *
     *     Does not affect the cached database (or any other user's item).
     *
     *     Name must pass validation:
     *
     *         Above 1 character long.
     *
     *         Below 200 characters long.
     *
     *         Cannot contain any non-url or non filesystem safe characters. (This is removed automatically).
     *
     *         Cannot contain any leading or trailing whitespace. You can have spaces in the middle. (This is removed automatically).
     *
     *     Tags follow the same rules as the name.
     *
     *     Alternative hashes must be of MD5, SHA1, or SHA256.
     *
     * [online documentation](https://api-docs.torbox.app/#d770d5df-390f-4670-b89e-d16e38ff22ba)
     */
    suspend fun editWeb(
        webId: Int,
        name: String? = null,
        tags: List<String>? = null,
        alternativeHashes: List<String>? = null,
    ): ApiResult<GenericResponseDto>
}
