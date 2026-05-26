package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.CreateWebDownloadResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.DownloadResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.WebDownloadListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface WebApi {
    suspend fun getWebLinks(
        bypassCache: String? = null,
        id: Int? = null,
        offset: Int? = null,
        limit: Int? = null,
    ): ApiResult<WebDownloadListResponseDto>

    suspend fun createWebLink(
        link: String,
        password: String? = null,
        name: String? = null,
        asQueued: Boolean? = null,
        addOnlyIfCached: Boolean? = null,
    ): ApiResult<CreateWebDownloadResponseDto>

    suspend fun downloadWebLink(
        token: String,
        webId: Int,
        fileId: Int? = null,
        zipLink: String? = null,
        userIp: String? = null,
        redirect: Boolean? = null,
        appendName: Boolean? = null,
    ): ApiResult<DownloadResponseDto>

    /**
     * Controls a web item
     *
     * https://api-docs.torbox.app/#151c9e58-16ca-4e44-91b0-6192fddc5e32
     */
    suspend fun controlWebLink(
        webId: Int? = null,
        operation: String,
        all: Boolean? = null,
    ): ApiResult<GenericResponseDto>
}
