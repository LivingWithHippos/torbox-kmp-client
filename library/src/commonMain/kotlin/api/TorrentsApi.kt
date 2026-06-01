package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseMapDto
import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityDto
import com.github.livingwithhippos.torbox.kmp.client.model.CreateTorrentDto
import com.github.livingwithhippos.torbox.kmp.client.model.ExportTorrentResultDto
import com.github.livingwithhippos.torbox.kmp.client.model.TorrentDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface TorrentsApi {
    /**
     * Creates a torrent asynchronously under your account.
     * Simply send either a [magnet] link, or a torrent file.
     * Once they have been checked, they will begin downloading assuming your account has available active download slots, and they aren't too large.
     *
     * The request will return instantly, not returning any data other than a success message.
     * This is the same for failures, as they will return the error in the background, rather than immediately.
     *
     * [online documentation](https://api-docs.torbox.app/#00b518c6-15b3-4215-b72b-d42e6efb8119)
     */
    suspend fun asyncCreate(
        fileBytes: ByteArray? = null,
        fileName: String? = null,
        magnet: String? = null,
        seed: Int? = null,
        allowZip: Boolean? = null,
        name: String? = null,
        asQueued: Boolean? = null,
        addOnlyIfCached: Boolean? = null,
    ): ApiResult<ApiResponseDto<String>>

    /**
     * Creates a torrent under your account.
     * Simply send **either** a magnet link, or a torrent file. Once they have bee
     * n checked, they will begin downloading assuming your account has available active download slots, and they aren't too large.
     *
     * [online documentation](https://api-docs.torbox.app/#062b717f-4866-4fc0-a3e6-6e4c2520eefa)
     */
    suspend fun create(
        fileBytes: ByteArray? = null,
        fileName: String? = null,
        magnet: String? = null,
        seed: Int? = null,
        allowZip: Boolean? = null,
        name: String? = null,
        asQueued: Boolean? = null,
        addOnlyIfCached: Boolean? = null,
    ): ApiResult<ApiResponseDto<CreateTorrentDto>>

    /**
     * Controls a torrent.
     * By sending the torrent's ID and the type of operation you want to perform, it will send that request to the torrent client.
     *
     * Operations are either:
     *
     *     **reannounce** reannounces the torrent to get new peers
     *
     *     **delete** deletes the torrent from the client and your account permanently
     *
     *     **resume** resumes a paused torrent
     *
     *     **stop_seeding** stops seeding a torrent
     *
     * [online documentation](https://api-docs.torbox.app/)
     */
    suspend fun control(
        torrentId: Int? = null,
        operation: String,
        all: Boolean? = null,
    ): ApiResult<ApiResponseDto<String>>

    /**
     * Updates a download item based on the item's ID.
     * Sending a PUT request with valid items overwrites the previous items entirely, so use this endpoint with caution.
     * Please make sure to read the restrictions below as they are important.
     *
     * **Restrictions**
     *
     *     - Item must be cached to update.
     *
     *     - Does not affect the cached database (or any other user's item).
     *
     *     - Name must pass validation:
     *
     *            + Above 1 character long.
     *
     *            + Below 200 characters long.
     *
     *            + Cannot contain any non-url or non filesystem safe characters. (This is removed automatically).
     *
     *            + Cannot contain any leading or trailing whitespace. You can have spaces in the middle. (This is removed automatically).
     *
     *     - Tags follow the same rules as the name.
     *
     *     - Alternative hashes must be of MD5, SHA1, or SHA256.
     *
     * [online documentation](https://api-docs.torbox.app/#a436204c-b235-4323-91b0-769311740d68)
     */
    suspend fun edit(
        torrentId: Int,
        name: String? = null,
        tags: List<String>? = null,
        alternativeHashes: List<String>? = null,
    ): ApiResult<ApiResponseDto<String>>

    /**
     * Gets the user's torrent list. This gives you the needed information to perform other torrent actions.
     * This information only gets updated every 600 seconds, or when the Request Update On Torrent request is sent to the relay API.
     *
     * Download States:
     *
     *     "downloading" -> The torrent is currently downloading.
     *
     *     "uploading" -> The torrent is currently seeding.
     *
     *     "stalled (no seeds)" -> The torrent is trying to download, but there are no seeds connected to download from.
     *
     *     "paused" -> The torrent is paused.
     *
     *     "completed" -> The torrent is completely downloaded. Do not use this for download completion status.
     *
     *     "cached" -> The torrent is cached from the server.
     *
     *     "metaDL" -> The torrent is downloading metadata from the hoard.
     *
     *     "checkingResumeData" -> The torrent is checking resumable data.
     *
     * All other statuses are basic qBittorrent states. [Check out here for the full list](https://github.com/qbittorrent/qBittorrent/wiki/WebUI-API-(qBittorrent-4.1)#torrent-management).
     *
     * [online documentation](https://api-docs.torbox.app/#8fa2961a-da1e-410e-84c3-821b3f6b30cc)
     */
    suspend fun list(
        bypassCache: String? = null,
        offset: Int? = null,
        limit: Int? = null,
    ): ApiResult<ApiResponseListDto<TorrentDto>>

    /**
     * Get a user's torrent by id.
     * This information only gets updated every 600 seconds, or when the Request Update On Torrent request is sent to the relay API.
     *
     * Download States:
     *
     *     "downloading" -> The torrent is currently downloading.
     *
     *     "uploading" -> The torrent is currently seeding.
     *
     *     "stalled (no seeds)" -> The torrent is trying to download, but there are no seeds connected to download from.
     *
     *     "paused" -> The torrent is paused.
     *
     *     "completed" -> The torrent is completely downloaded. Do not use this for download completion status.
     *
     *     "cached" -> The torrent is cached from the server.
     *
     *     "metaDL" -> The torrent is downloading metadata from the hoard.
     *
     *     "checkingResumeData" -> The torrent is checking resumable data.
     *
     * All other statuses are basic qBittorrent states. [Check out here for the full list](https://github.com/qbittorrent/qBittorrent/wiki/WebUI-API-(qBittorrent-4.1)#torrent-management).
     *
     * [online documentation](https://api-docs.torbox.app/#8fa2961a-da1e-410e-84c3-821b3f6b30cc)
     */
    suspend fun get(
        // todo: check if this is supported when using the id
        bypassCache: String? = null,
        id: Int,
    ): ApiResult<ApiResponseDto<TorrentDto>>

    /**
     * Requests the download link from the server.
     * Because downloads are metered, TorBox cannot afford to allow free access to the links directly.
     * This endpoint opens the link for 3 hours for downloads.
     * Once a download is started, the user has nearly unlimited time to download the file.
     * The 1-hour time limit is simply for starting downloads. This prevents long term link sharing.
     *
     * **Permalinks**
     *
     * Instead of generating many CDN urls by requesting this endpoint, you can instead create a permalink such as: https://api.torbox.app/v1/api/torrents/requestdl?token=APIKEY&torrent_id=NUMBER&file_id=NUMBER&redirect=true and when a user clicks on it, it will automatically redirect them to the CDN link.
     * This saves requests and doesn't abuse the API. Use this method rather than saving CDN links as they are not permanent. To invalidate these permalinks, simply reset your API token or delete the item from your dashboard.
     *
     * [online documentation](https://api-docs.torbox.app/#cc4ceac8-7cb6-4be5-bf09-f949d9e9a458)
     */
    suspend fun downloadLink(
        token: String,
        torrentId: Int,
        fileId: Int? = null,
        zipLink: String? = null,
        userIp: String? = null,
        redirect: Boolean? = null,
        appendName: Boolean? = null,
    ): ApiResult<ApiResponseDto<String>>

    /**
     * Takes in a list of comma separated torrent hashes and checks if the torrent is cached.
     * This endpoint only gets a max of around 100 at a time, due to http limits in queries.
     *
     * Performance is very fast. Less than 1 second per 100.
     * Time is approximately O(log n) time for those interested in taking it to its max.
     * That is without caching as well.
     * This endpoint stores a cache for an hour.
     *
     * [online documentation](https://api-docs.torbox.app/#99e1b7a7-bdf6-4884-af9d-bd9903a8ca63)
     */
    suspend fun listAvailability(
        hashes: List<String>,
        listFiles: Boolean? = null,
    ): ApiResult<ApiResponseListDto<CacheAvailabilityDto>>

    /**
     * Takes in a list of comma separated torrent hashes and checks if the torrent is cached.
     * This endpoint only gets a max of around 100 at a time, due to http limits in queries.
     *
     * Performance is very fast. Less than 1 second per 100.
     * Time is approximately O(log n) time for those interested in taking it to its max.
     * That is without caching as well.
     * This endpoint stores a cache for an hour.
     *
     * [online documentation](https://api-docs.torbox.app/#99e1b7a7-bdf6-4884-af9d-bd9903a8ca63)
     */
    suspend fun listAvailabilityObject(
        hashes: List<String>,
        listFiles: Boolean? = null,
    ): ApiResult<ApiResponseMapDto<CacheAvailabilityDto>>

    /**
     * Takes in a list of comma separated torrent hashes and checks if the torrent is cached.
     * With the POST endpoint you can submit a nearly unlimited hashes compared to the GET endpoint which is limited by url parameters.
     *
     * Performance is very fast. Less than 1 second per 100.
     * Time is approximately O(log n) time for those interested in taking it to its max.
     * That is without caching as well.
     * This endpoint stores a cache for an hour.
     *
     * [online documentation](https://api-docs.torbox.app/#00718c3f-0147-4423-af4d-e50b94fea867)
     */
    suspend fun listAvailabilityBatchObject(hashes: List<String>): ApiResult<ApiResponseMapDto<CacheAvailabilityDto>>

    /**
     * Takes in a list of comma separated torrent hashes and checks if the torrent is cached.
     * With the POST endpoint you can submit a nearly unlimited hashes compared to the GET endpoint which is limited by url parameters.
     *
     * Performance is very fast. Less than 1 second per 100.
     * Time is approximately O(log n) time for those interested in taking it to its max.
     * That is without caching as well.
     * This endpoint stores a cache for an hour.
     *
     * [online documentation](https://api-docs.torbox.app/)
     */
    suspend fun listAvailabilityBatch(hashes: List<String>): ApiResult<ApiResponseListDto<CacheAvailabilityDto>>

    /**
     * Exports the magnet or torrent file.
     * Requires a [type] to be passed:
     *
     * - "magnet" -> [ExportTorrentResultDto.Magnet]
     * - "file"   -> [ExportTorrentResultDto.File]
     *
     * [online documentation](https://api-docs.torbox.app/#3847e19a-0de7-4956-a072-4c4d41d0ff6c)
     */
    suspend fun export(
        torrentId: Int,
        type: String,
    ): ApiResult<ExportTorrentResultDto>

    /**
     * Converts any [magnet] to a torrent file.
     * Does not require the magnet to be on your TorBox account or in the TorBox cache.
     * Free endpoint for public use.
     *
     * [online documentation](https://api-docs.torbox.app/#543e7f9f-a875-4112-8570-34a32b28764b)
     */
    suspend fun magnetToFile(magnet: String): ApiResult<ByteArray>
}
