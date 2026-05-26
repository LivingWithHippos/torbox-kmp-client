package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.CacheAvailabilityObjectResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.CreateWebDownloadResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.DownloadResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.HostersResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.WebDownloadListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface WebApi {
    suspend fun createWebLink(
        link: String,
        password: String? = null,
        name: String? = null,
        asQueued: Boolean? = null,
        addOnlyIfCached: Boolean? = null,
    ): ApiResult<CreateWebDownloadResponseDto>

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

    suspend fun getWebLinks(
        bypassCache: String? = null,
        id: Int? = null,
        offset: Int? = null,
        limit: Int? = null,
    ): ApiResult<WebDownloadListResponseDto>

    suspend fun requestDownloadLink(
        token: String,
        webId: Int,
        fileId: Int? = null,
        zipLink: String? = null,
        userIp: String? = null,
        redirect: Boolean? = null,
        appendName: Boolean? = null,
    ): ApiResult<DownloadResponseDto>

    suspend fun getWebAvailabilityList(
        hashes: List<String>,
        listFiles: Boolean? = null,
    ): ApiResult<CacheAvailabilityListResponseDto>

    suspend fun getWebAvailabilityObject(
        hashes: List<String>,
        listFiles: Boolean? = null,
    ): ApiResult<CacheAvailabilityObjectResponseDto>

    suspend fun getWebAvailabilityBatchObject(hashes: List<String>): ApiResult<CacheAvailabilityObjectResponseDto>

    suspend fun getWebAvailabilityBatchList(hashes: List<String>): ApiResult<CacheAvailabilityListResponseDto>

    suspend fun getHosters(auth: Boolean = false): ApiResult<HostersResponseDto>
}
