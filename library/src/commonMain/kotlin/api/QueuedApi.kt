package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.QueuedDownloadDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface QueuedApi {
    /**
     * Retrieves all of a user's queued downloads by type.
     * If you want to get all 3 types, "torrent", "usenet" and "webdl" then you will need to run this request 3 times, each with the different type.
     *
     * [online documentation](https://api-docs.torbox.app/#097e3b5d-c34d-449a-b26b-603ed3e6f6fe)
     */
    suspend fun downloads(
        bypassCache: Boolean? = null,
        id: Int? = null,
        offset: Int? = null,
        limit: Int? = null,
        type: String = "torrent",
    ): ApiResult<ApiResponseListDto<QueuedDownloadDto>>

    /**
     * Controls a queued torrent.
     * By sending the queued torrent's ID and the type of operation you want to perform, it will perform that action on the queued torrent.
     *
     * Operations are either:
     *
     *     - Delete deletes the queued download from your account
     *     - Start starts a queued download, cannot be used with the "all" parameter
     *
     * [online documentation](https://api-docs.torbox.app/#2e9b7b90-5544-478d-8744-e04543f4f0b8)
     */
    suspend fun controlQueued(
        queuedId: Int? = null,
        operation: String,
        all: Boolean? = null,
    ): ApiResult<GenericResponseDto>
}
