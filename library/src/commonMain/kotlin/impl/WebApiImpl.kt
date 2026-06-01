package com.github.livingwithhippos.torbox.kmp.client.impl

import com.github.livingwithhippos.torbox.kmp.client.api.WebApi
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityDto
import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityObjectResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.CreateWebDownloadDto
import com.github.livingwithhippos.torbox.kmp.client.model.EditWebRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.HosterDto
import com.github.livingwithhippos.torbox.kmp.client.model.WebControlRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.WebDownloadDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult
import com.github.livingwithhippos.torbox.kmp.client.network.executeRequest
import com.github.livingwithhippos.torbox.kmp.client.network.noAuth
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class WebApiImpl(
    private val client: HttpClient,
) : WebApi {
    override suspend fun listWeb(
        bypassCache: String?,
        offset: Int?,
        limit: Int?,
    ): ApiResult<ApiResponseListDto<WebDownloadDto>> =
        executeRequest {
            client.get("webdl/mylist") {
                // todo: check if this changes the response format
                if (bypassCache != null) {
                    parameter("bypass_cache", "")
                }
                parameter("offset", offset)
                parameter("limit", limit)
            }
        }

    override suspend fun getWeb(
        bypassCache: String?,
        id: Int,
    ): ApiResult<ApiResponseDto<WebDownloadDto>> =
        executeRequest {
            client.get("webdl/mylist") {
                if (bypassCache != null) {
                    parameter("bypass_cache", "")
                }
                parameter("id", id)
            }
        }

    override suspend fun create(
        link: String,
        password: String?,
        name: String?,
        asQueued: Boolean?,
        addOnlyIfCached: Boolean?,
    ): ApiResult<ApiResponseDto<CreateWebDownloadDto>> =
        executeRequest {
            client.post("webdl/createwebdownload") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("link", link)
                            password?.takeIf { it.isNotBlank() }?.let { append("password", it) }
                            name?.takeIf { it.isNotBlank() }?.let { append("name", it) }
                            asQueued?.let { append("as_queued", it.toString()) }
                            addOnlyIfCached?.let { append("add_only_if_cached", it.toString()) }
                        },
                    ),
                )
            }
        }

    override suspend fun requestDownloadLink(
        token: String,
        webId: Int,
        fileId: Int?,
        zipLink: String?,
        userIp: String?,
        redirect: Boolean?,
        appendName: Boolean?,
    ): ApiResult<GenericResponseDto> =
        executeRequest {
            client.get("webdl/requestdl") {
                parameter("token", token)
                parameter("web_id", webId)
                parameter("file_id", fileId)
                parameter("zip_link", zipLink)
                parameter("user_ip", userIp)
                parameter("redirect", redirect)
                parameter("append_name", appendName)
            }
        }

    override suspend fun listWebAvailability(
        hashes: List<String>,
        listFiles: Boolean?,
    ): ApiResult<ApiResponseListDto<CacheAvailabilityDto>> =
        executeRequest {
            client.get("webdl/checkcached") {
                hashes.forEach {
                    parameter("hash", it)
                }
                parameter("format", "list")
                parameter("list_files", listFiles)
            }
        }

    override suspend fun listWebAvailabilityObject(
        hashes: List<String>,
        listFiles: Boolean?,
    ): ApiResult<CacheAvailabilityObjectResponseDto> =
        executeRequest {
            client.get("webdl/checkcached") {
                hashes.forEach {
                    parameter("hash", it)
                }
                parameter("format", "object")
                parameter("list_files", listFiles)
            }
        }

    override suspend fun listWebAvailabilityBatchObject(hashes: List<String>): ApiResult<CacheAvailabilityObjectResponseDto> =
        executeRequest {
            client.post("webdl/checkcached") {
                contentType(ContentType.Application.Json)
                setBody(
                    CacheAvailabilityRequestDto(
                        format = "object",
                        hashes = hashes,
                    ),
                )
            }
        }

    override suspend fun listWebAvailabilityBatch(hashes: List<String>): ApiResult<ApiResponseListDto<CacheAvailabilityDto>> =
        executeRequest {
            client.post("webdl/checkcached") {
                contentType(ContentType.Application.Json)
                setBody(
                    CacheAvailabilityRequestDto(
                        format = "list",
                        hashes = hashes,
                    ),
                )
            }
        }

    override suspend fun hosters(auth: Boolean): ApiResult<ApiResponseListDto<HosterDto>> =
        executeRequest {
            client.get("webdl/hosters") {
                if (!auth) {
                    noAuth()
                }
            }
        }

    override suspend fun editWeb(
        webId: Int,
        name: String?,
        tags: List<String>?,
        alternativeHashes: List<String>?,
    ): ApiResult<GenericResponseDto> {
        if (name == null && tags == null && alternativeHashes == null) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("At least one between name, tags, or alternativeHashes must be provided"),
            )
        }

        return executeRequest {
            client.put("webdl/editwebdownload") {
                contentType(ContentType.Application.Json)
                setBody(
                    EditWebRequestDto(
                        webId = webId,
                        name = name,
                        tags = tags,
                        alternativeHashes = alternativeHashes,
                    ),
                )
            }
        }
    }

    override suspend fun controlWebLink(
        webId: Int?,
        operation: String,
        all: Boolean?,
    ): ApiResult<GenericResponseDto> {
        val normalizedOperation = operation.trim().lowercase()
        if (normalizedOperation != "delete") {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Operation must be one of: delete"),
            )
        } else if (webId == null && all != true) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Either a web id or all = true must be set"),
            )
        }

        return executeRequest {
            client.post("webdl/controlwebdownload") {
                contentType(ContentType.Application.Json)
                setBody(
                    WebControlRequestDto(
                        webId = webId,
                        operation = normalizedOperation,
                        all = all,
                    ),
                )
            }
        }
    }
}
