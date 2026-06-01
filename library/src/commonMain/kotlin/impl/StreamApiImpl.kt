package com.github.livingwithhippos.torbox.kmp.client.impl

import com.github.livingwithhippos.torbox.kmp.client.api.StreamApi
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.CreatedStreamDto
import com.github.livingwithhippos.torbox.kmp.client.model.StreamDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult
import com.github.livingwithhippos.torbox.kmp.client.network.executeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class StreamApiImpl(
    private val client: HttpClient,
) : StreamApi {
    override suspend fun create(
        id: Int,
        fileId: Int,
        type: String,
        chosenSubtitleIndex: Int?,
        chosenAudioIndex: Int,
        chosenResolutionIndex: Int?,
    ): ApiResult<ApiResponseDto<CreatedStreamDto>> {
        val normalizedType = type.trim().lowercase()
        if (id <= 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("id must be a positive integer"),
            )
        }
        if (fileId < 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("fileId must be >= 0"),
            )
        }
        if (normalizedType !in listOf("torrent", "usenet", "webdl")) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("type must be one of: torrent, usenet, webdl"),
            )
        }
        if (chosenAudioIndex < 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("chosenAudioIndex must be >= 0"),
            )
        }
        if (chosenSubtitleIndex != null && chosenSubtitleIndex < 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("chosenSubtitleIndex must be >= 0"),
            )
        }
        if (chosenResolutionIndex != null && chosenResolutionIndex < 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("chosenResolutionIndex must be >= 0"),
            )
        }

        return executeRequest {
            client.get("stream/createstream") {
                parameter("id", id)
                parameter("file_id", fileId)
                parameter("type", normalizedType)
                chosenSubtitleIndex?.let { parameter("chosen_subtitle_index", it) }
                parameter("chosen_audio_index", chosenAudioIndex)
                chosenResolutionIndex?.let { parameter("chosen_resolution_index", it) }
            }
        }
    }

    override suspend fun get(
        presignedToken: String,
        token: String,
        chosenSubtitleIndex: Int?,
        chosenAudioIndex: Int?,
        chosenResolutionIndex: Int?,
    ): ApiResult<ApiResponseDto<StreamDto>> {
        if (presignedToken.isBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("presignedToken must not be blank"),
            )
        }
        if (token.isBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("token must not be blank"),
            )
        }
        if (chosenSubtitleIndex != null && chosenSubtitleIndex < 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("chosenSubtitleIndex must be >= 0"),
            )
        }
        if (chosenAudioIndex != null && chosenAudioIndex < 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("chosenAudioIndex must be >= 0"),
            )
        }
        if (chosenResolutionIndex != null && chosenResolutionIndex < 0) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("chosenResolutionIndex must be >= 0"),
            )
        }

        return executeRequest {
            client.get("stream/getstreamdata") {
                parameter("presigned_token", presignedToken)
                parameter("token", token)
                chosenSubtitleIndex?.let { parameter("chosen_subtitle_index", it) }
                chosenAudioIndex?.let { parameter("chosen_audio_index", it) }
                chosenResolutionIndex?.let { parameter("chosen_resolution_index", it) }
            }
        }
    }
}
