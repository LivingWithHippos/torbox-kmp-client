package com.github.livingwithhippos.torbox.kmp.client.impl

import com.github.livingwithhippos.torbox.kmp.client.api.UsenetApi
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseMapDto
import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityDto
import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.CreateUsenetDto
import com.github.livingwithhippos.torbox.kmp.client.model.EditUsenetRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.UsenetControlRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.UsenetDownloadDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult
import com.github.livingwithhippos.torbox.kmp.client.network.executeRequest
import io.ktor.client.HttpClient
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

class UsenetApiImpl(
    private val client: HttpClient,
) : UsenetApi {
    override suspend fun list(
        bypassCache: String?,
        offset: Int?,
        limit: Int?,
    ): ApiResult<ApiResponseListDto<UsenetDownloadDto>> =
        executeRequest {
            client.get("usenet/mylist") {
                if (bypassCache != null) {
                    parameter("bypass_cache", "")
                }
                parameter("offset", offset)
                parameter("limit", limit)
            }
        }

    override suspend fun get(
        bypassCache: String?,
        id: Int,
    ): ApiResult<ApiResponseDto<UsenetDownloadDto>> =
        executeRequest {
            client.get("usenet/mylist") {
                if (bypassCache != null) {
                    parameter("bypass_cache", "")
                }
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
        }

        return executeRequest {
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
                            name?.takeIf { it.isNotBlank() }?.let { append("name", it) }
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

        return executeRequest {
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
        zipLink: String?,
        userIp: String?,
        redirect: Boolean?,
        appendName: Boolean?,
    ): ApiResult<ApiResponseDto<String>> =
        executeRequest {
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
    ): ApiResult<ApiResponseListDto<CacheAvailabilityDto>> =
        executeRequest {
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
    ): ApiResult<ApiResponseMapDto<CacheAvailabilityDto>> =
        executeRequest {
            client.get("usenet/checkcached") {
                hashes.forEach {
                    parameter("hash", it)
                }
                parameter("format", "object")
                parameter("list_files", listFiles)
            }
        }

    override suspend fun listAvailabilityBatchObject(hashes: List<String>): ApiResult<ApiResponseMapDto<CacheAvailabilityDto>> =
        executeRequest {
            client.post("usenet/checkcached") {
                contentType(ContentType.Application.Json)
                setBody(
                    CacheAvailabilityRequestDto(
                        format = "object",
                        hashes = hashes,
                    ),
                )
            }
        }

    override suspend fun listAvailabilityBatch(hashes: List<String>): ApiResult<ApiResponseListDto<CacheAvailabilityDto>> =
        executeRequest {
            client.post("usenet/checkcached") {
                contentType(ContentType.Application.Json)
                setBody(
                    CacheAvailabilityRequestDto(
                        format = "list",
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

        return executeRequest {
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
