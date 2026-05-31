package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.CreatedStreamDto
import com.github.livingwithhippos.torbox.kmp.client.model.StreamDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface StreamApi {
    /**
     * Creates a new stream based off of the data provided.
     *
     * **Selecting Subtitles and Audios**
     *
     * We use indexes to determine which audio and subtitle you would like to select. To view all available indexes, you must request this endpoint intially with "null" or 0, for the chosen indexes. This will return a metadata dictionary in the response, which will contain the list of audios and subtitles available for this stream. When choosing a subtitle or audio stream, use the relative index, starting from 0, rather than the "index" value. This is important for selecting the correct index. Once your user gets a selection of audios and subtitles available, you can submit this to the endpoint again to get the chosen stream.
     *
     *     null - Selecting no subtitle. Not available for audio as one must always be selected.
     *
     *     0 - Selecting first subtitle or audio
     *
     * **Selecting Resolution**
     *
     * Similar to the subtitles and audios, we use indexes to determine the resolution chosen. Unlike the above, these are predetermined. We do not upscale video, so if for example, you pass a 1080p video, and request it with resolution, 7, it will stay at 1080p. Please see the below for options.
     *
     *     null - Original video resolution. No downscaling will take place.
     *
     *     0 - 144p
     *
     *     1 - 240p
     *
     *     2 - 360p
     *
     *     3 - 480p
     *
     *     4 - 720p
     *
     *     5 - 1080p
     *
     *     6 - 1440p
     *
     *     7 - 2160p
     *
     * **Types**
     *
     * This endpoint allows you to handle torrents, usenet and web downloads all in one. This is why the "type" parameter is available. It defaults to "torrent".
     *
     *     torrent - Torrents
     *
     *     usenet - Usenet downloads
     *
     *     webdownload - Web Downloads
     *
     * **Important Notes**
     *
     *     Only text based subtitles are supported. If a non-text based subtitle is selected, it will be ignored.
     *
     *     If a subtitle or audio index does not exist on the stream, it will be ignored.
     *
     *     There are a lot of edge-case videos, if something doesn't work, don't hesitate to report the download hash and file_id of the failing video file.
     *
     *     This is a Pro only feature.
     *
     * [online documentation](https://api-docs.torbox.app/#52c32a7a-54da-4acc-bf70-2bed0b6c6dd2)
     */
    suspend fun create(
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
    suspend fun get(
        presignedToken: String,
        token: String,
        chosenSubtitleIndex: Int? = null,
        chosenAudioIndex: Int? = null,
        chosenResolutionIndex: Int? = null,
    ): ApiResult<ApiResponseDto<StreamDto>>
}
