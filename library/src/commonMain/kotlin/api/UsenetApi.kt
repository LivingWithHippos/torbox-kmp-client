package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityDto
import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityObjectResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.CreateUsenetDto
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.UsenetDownloadDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface UsenetApi {
    /**
     * Gets the user's usenet download list.
     * This gives you the needed information to perform other usenet actions.
     * Unlike Torrents, this information is updated on its own every 5 seconds for live usenet downloads.
     *
     * [online documentation](https://api-docs.torbox.app/#b9de037f-fd97-4236-bd11-04535c9fe551)
     */
    suspend fun listUsenet(
        bypassCache: String? = null,
        offset: Int? = null,
        limit: Int? = null,
    ): ApiResult<ApiResponseListDto<UsenetDownloadDto>>

    /**
     * Gets a user's usenet download.
     * This gives you the needed information to perform other usenet actions.
     * Unlike Torrents, this information is updated on its own every 5 seconds for live usenet downloads.
     *
     * [online documentation](https://api-docs.torbox.app/#b9de037f-fd97-4236-bd11-04535c9fe551)
     */
    suspend fun getUsenet(
        bypassCache: String? = null,
        id: Int,
    ): ApiResult<ApiResponseDto<UsenetDownloadDto>>

    /**
     * Creates a usenet download under your account.
     * Simply send either a link, or a nzb file.
     * Once they have been checked, they will begin downloading assuming your account has available active download slots, and they aren't too large.
     *
     * **Post Processing Options:**
     *
     * All post-processing options that the Usenet client will perform before TorBox's own processing to make the files available. It is recommended you either don't send this parameter, or keep it at -1 for default, which will give only the wanted files.
     *
     *     -1, Default. This runs repairs, and extractions as well as deletes the source files leaving only the wanted downloaded files.
     *
     *     0, None. No post-processing at all. Just download all the files (including all PAR2). TorBox will still do its normal processing to make the download available, but no repairs, or extraction will take place.
     *
     *     1, Repair. Download files and do a PAR2 verification. If the verification fails, download more PAR2 files and attempt to repair the files.
     *
     *     2, Repair and Unpack. Download all files, do a PAR2 verification and unpack the files. The final folder will also include the RAR and ZIP files.
     *
     *     3, Repair, Unpack and Delete. Download all files, do a PAR2 verification, unpack the files to the final folder and delete the source files.
     *
     * [online documentation](https://api-docs.torbox.app/#daacc348-3e3c-476e-8362-f35124a8ca46)
     */
    suspend fun create(
        fileBytes: ByteArray? = null,
        fileName: String? = null,
        link: String? = null,
        name: String? = null,
        password: String? = null,
        postProcessing: Int? = null,
        asQueued: Boolean? = null,
        addOnlyIfCached: Boolean? = null,
    ): ApiResult<ApiResponseDto<CreateUsenetDto>>

    /**
     * Controls a usenet download.
     * By sending the usenet download's ID and the type of operation you want to perform, it will send that request to the usenet client.
     *
     * Operations are either:
     *
     *     Delete deletes the nzb from the client and your account permanently
     *
     * [online documentation](https://api-docs.torbox.app/#39a94784-ff64-4387-80e1-b7cfb1232dbe)
     */
    suspend fun controlUsenet(
        usenetId: Int? = null,
        operation: String,
        all: Boolean? = null,
    ): ApiResult<GenericResponseDto>

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
     * [online documentation](https://api-docs.torbox.app/#6fb5deaf-ea44-4cc8-a2c0-3a262a2ec521)
     */
    suspend fun usenetDownloadLink(
        token: String,
        // todo: check if this is a string (it is in the control body JSON documentation) and update it everywhere
        usenetId: Int,
        fileId: Int? = null,
        zipLink: String? = null,
        userIp: String? = null,
        redirect: Boolean? = null,
        appendName: Boolean? = null,
    ): ApiResult<GenericResponseDto>

    /**
     * Takes in a list of comma separated usenet hashes and checks if the usenet download is cached.
     * This endpoint only gets a max of around 100 at a time, due to http limits in queries.
     *
     * Performance is very fast. Less than 1 second per 100.
     * Time is approximately O(log n) time for those interested in taking it to its max.
     * That is without caching as well.
     * This endpoint stores a cache for an hour.
     *
     * To get the hash of a usenet download, you can [read this document](https://support.torbox.app/en/articles/13681109-technical-getting-hashes-for-searches) for all the strategies.
     *
     * [online documentation](https://api-docs.torbox.app/#b9de037f-fd97-4236-bd11-04535c9fe551)
     *
     * Endpoint duplicated because of different response format in [getAvailabilityObject]
     */
    suspend fun listUsenetAvailability(
        hashes: List<String>,
        listFiles: Boolean? = null,
    ): ApiResult<ApiResponseListDto<CacheAvailabilityDto>>

    /**
     * Takes in a list of comma separated usenet hashes and checks if the usenet download is cached.
     * This endpoint only gets a max of around 100 at a time, due to http limits in queries.
     *
     * Performance is very fast. Less than 1 second per 100.
     * Time is approximately O(log n) time for those interested in taking it to its max.
     * That is without caching as well.
     * This endpoint stores a cache for an hour.
     *
     * To get the hash of a usenet download, you can [read this document](https://support.torbox.app/en/articles/13681109-technical-getting-hashes-for-searches) for all the strategies.
     *
     * [online documentation](https://api-docs.torbox.app/#b9de037f-fd97-4236-bd11-04535c9fe551)
     *
     * Endpoint duplicated because of different response format in [getAvailabilityList]
     */
    suspend fun listUsenetAvailabilityObject(
        hashes: List<String>,
        listFiles: Boolean? = null,
    ): ApiResult<CacheAvailabilityObjectResponseDto>

    /**
     * Takes in a list of comma separated usenet hashes and checks if the usenet download is cached.
     * With the POST endpoint you can submit a nearly unlimited hashes compared to the GET endpoint which is limited by url parameters.
     *
     * Performance is very fast. Less than 1 second per 100.
     * Time is approximately O(log n) time for those interested in taking it to its max.
     * That is without caching as well.
     * This endpoint stores a cache for an hour.
     *
     * [online documentation](https://api-docs.torbox.app/#11ddb96a-c3b0-4ba5-99a1-43338321d192)
     *
     * Endpoint duplicated because of different response format in [getAvailabilityBatchList]
     */
    suspend fun listUsenetAvailabilityBatchObject(hashes: List<String>): ApiResult<CacheAvailabilityObjectResponseDto>

    /**
     * Takes in a list of comma separated usenet hashes and checks if the usenet download is cached.
     * With the POST endpoint you can submit a nearly unlimited hashes compared to the GET endpoint which is limited by url parameters.
     *
     * Performance is very fast. Less than 1 second per 100.
     * Time is approximately O(log n) time for those interested in taking it to its max.
     * That is without caching as well.
     * This endpoint stores a cache for an hour.
     *
     * [online documentation](https://api-docs.torbox.app/#11ddb96a-c3b0-4ba5-99a1-43338321d192)
     *
     * Endpoint duplicated because of different response format in [getAvailabilityBatchObject]
     */
    suspend fun listUsenetAvailabilityBatch(hashes: List<String>): ApiResult<ApiResponseListDto<CacheAvailabilityDto>>

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
     * [online documentation](https://api-docs.torbox.app/#cf0ab144-4e02-4fe7-aaab-738d7073918b)
     */
    suspend fun editUsenet(
        usenetId: Int,
        name: String? = null,
        tags: List<String>? = null,
        alternativeHashes: List<String>? = null,
    ): ApiResult<GenericResponseDto>
}
