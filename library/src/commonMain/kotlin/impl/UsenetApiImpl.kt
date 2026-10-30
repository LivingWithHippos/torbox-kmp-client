package com.github.livingwithhippos.torbox.impl

import com.github.livingwithhippos.torbox.api.UsenetApi
import com.github.livingwithhippos.torbox.model.ApiResponseDto
import com.github.livingwithhippos.torbox.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.model.ApiResponseMapDto
import com.github.livingwithhippos.torbox.model.CacheDto
import com.github.livingwithhippos.torbox.model.CacheRequestDto
import com.github.livingwithhippos.torbox.model.CreateUsenetDto
import com.github.livingwithhippos.torbox.model.EditUsenetRequestDto
import com.github.livingwithhippos.torbox.model.UsenetControlRequestDto
import com.github.livingwithhippos.torbox.model.UsenetDownloadDto
import com.github.livingwithhippos.torbox.network.ApiResult
import com.github.livingwithhippos.torbox.network.executeRequest
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

internal class UsenetApiImpl(
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher,
) : UsenetApi {
    override suspend fun list(
        bypassCache: Boolean?,
        offset: Int?,
        limit: Int?,
    ): ApiResult<ApiResponseListDto<UsenetDownloadDto>> =
        executeRequest(dispatcher) {
            client.get("usenet/mylist") {
                bypassCache?.let { parameter("bypass_cache", it) }
                offset?.let { parameter("offset", it) }
                limit?.let { parameter("limit", it) }
            }
        }

    override suspend fun get(
        bypassCache: Boolean?,
        id: Int,
    ): ApiResult<ApiResponseDto<UsenetDownloadDto>> =
        executeRequest(dispatcher) {
            client.get("usenet/mylist") {
                bypassCache?.let { parameter("bypass_cache", it) }
                parameter("id", id)
            }
        }

    override suspend fun create(
        fileBytes: ByteArray?,
        fileName: String?,
        link: String?,
        name: String?,
        password: String?,
        postProcessing: Int?,
        asQueued: Boolean?,
        addOnlyIfCached: Boolean?,
    ): ApiResult<ApiResponseDto<CreateUsenetDto>> {
        if (fileBytes != null && !link.isNullOrBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Only one between fileBytes or link must be provided"),
            )
        } else if (fileBytes == null && link.isNullOrBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Either one between fileBytes or link must be provided"),
            )
        } else if (postProcessing != null && postProcessing !in listOf(-1, 0, 1, 2, 3)) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Post processing must be between -1 (default) and 3"),
            )
        }

        return executeRequest(dispatcher) {
            client.post("usenet/createusenetdownload") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            if (fileBytes != null) {
                                append(
                                    key = "file",
                                    value = fileBytes,
                                    headers =
                                        Headers.build {
                                            append(HttpHeaders.ContentType, "application/x-nzb")
                                            append(
                                                HttpHeaders.ContentDisposition,
                                                "filename=\"${fileName ?: "file.nzb"}\"",
                                            )
                                        },
                                )
                            }
                            link?.takeIf { it.isNotBlank() }?.let { append("link", it) }
                            name?.takeIf { it.isNotBlank() }?.let { append("name", it) }
                            password?.takeIf { it.isNotBlank() }?.let { append("password", it) }
                            postProcessing?.let { append("post_processing", it.toString()) }
                            asQueued?.let { append("as_queued", it.toString()) }
                            addOnlyIfCached?.let { append("add_only_if_cached", it.toString()) }
                        },
                    ),
                )
            }
        }
    }

    override suspend fun control(
        usenetId: Int?,
        operation: String,
        all: Boolean?,
    ): ApiResult<ApiResponseDto<String>> {
        if (listOf("delete").contains(operation.lowercase()).not()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Operation must be one of: delete"),
            )
        } else if (usenetId == null && all != true) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Either a usenet id or all = true must be set"),
            )
        }

        return executeRequest(dispatcher) {
            client.post("usenet/controlusenetdownload") {
                contentType(ContentType.Application.Json)
                setBody(
                    UsenetControlRequestDto(
                        usenetId = usenetId,
                        operation = operation.lowercase(),
                        all = all,
                    ),
                )
            }
        }
    }

    override suspend fun downloadLink(
        token: String,
        usenetId: Int,
        fileId: Int?,
        zipLink: Boolean?,
        userIp: String?,
        redirect: Boolean?,
        appendName: Boolean?,
    ): ApiResult<ApiResponseDto<String>> =
        executeRequest(dispatcher) {
            client.get("usenet/requestdl") {
                parameter("token", token)
                parameter("usenet_id", usenetId)
                parameter("file_id", fileId)
                parameter("zip_link", zipLink)
                parameter("user_ip", userIp)
                parameter("redirect", redirect)
                parameter("append_name", appendName)
            }
        }

    override suspend fun listAvailability(
        hashes: List<String>,
        listFiles: Boolean?,
    ): ApiResult<ApiResponseListDto<CacheDto>> =
        executeRequest(dispatcher) {
            client.get("usenet/checkcached") {
                hashes.forEach {
                    parameter("hash", it)
                }
                parameter("format", "list")
                parameter("list_files", listFiles)
            }
        }

    override suspend fun listAvailabilityObject(
        hashes: List<String>,
        listFiles: Boolean?,
    ): ApiResult<ApiResponseMapDto<CacheDto>> =
        executeRequest(dispatcher) {
            client.get("usenet/checkcached") {
                hashes.forEach {
                    parameter("hash", it)
                }
                parameter("format", "object")
                parameter("list_files", listFiles)
            }
        }

    override suspend fun listAvailabilityBatchObject(hashes: List<String>): ApiResult<ApiResponseMapDto<CacheDto>> =
        executeRequest(dispatcher) {
            client.post("usenet/checkcached") {
                parameter("format", "object")
                contentType(ContentType.Application.Json)
                setBody(
                    CacheRequestDto(
                        hashes = hashes,
                    ),
                )
            }
        }

    override suspend fun listAvailabilityBatch(hashes: List<String>): ApiResult<ApiResponseListDto<CacheDto>> =
        executeRequest(dispatcher) {
            client.post("usenet/checkcached") {
                parameter("format", "list")
                contentType(ContentType.Application.Json)
                setBody(
                    CacheRequestDto(
                        hashes = hashes,
                    ),
                )
            }
        }

    override suspend fun edit(
        usenetId: Int,
        name: String?,
        tags: List<String>?,
        alternativeHashes: List<String>?,
    ): ApiResult<ApiResponseDto<String>> {
        if (name == null && tags == null && alternativeHashes == null) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("At least one between name, tags, or alternativeHashes must be provided"),
            )
        }

        return executeRequest(dispatcher) {
            client.put("usenet/editusenetdownload") {
                contentType(ContentType.Application.Json)
                setBody(
                    EditUsenetRequestDto(
                        usenetId = usenetId,
                        name = name,
                        tags = tags,
                        alternativeHashes = alternativeHashes,
                    ),
                )
            }
        }
    }
}
