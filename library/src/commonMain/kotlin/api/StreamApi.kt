package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.CreatedStreamDto
import com.github.livingwithhippos.torbox.kmp.client.model.StreamDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface StreamApi {
    /**
     * Creates a new stream based off of the data provided.
     *
     * [online documentation](https://api-docs.torbox.app/#52c32a7a-54da-4acc-bf70-2bed0b6c6dd2)
     */
    suspend fun createStream(
        id: Int,
        fileId: Int = 0,
        type: String = "torrent",
        chosenSubtitleIndex: Int? = null,
        chosenAudioIndex: Int = 0,
        chosenResolutionIndex: Int? = null,
    ): ApiResult<ApiResponseDto<CreatedStreamDto>>

    /**
     * Gets stream data from an already created stream.
     * Requires the presigned token and user API token from the original request.
     *
     * [online documentation](https://api-docs.torbox.app/#1ead2b26-6ec0-4834-9136-1b087d066bad)
     */
    suspend fun getStream(
        presignedToken: String,
        token: String,
        chosenSubtitleIndex: Int? = null,
        chosenAudioIndex: Int? = null,
        chosenResolutionIndex: Int? = null,
    ): ApiResult<ApiResponseDto<StreamDto>>
}
