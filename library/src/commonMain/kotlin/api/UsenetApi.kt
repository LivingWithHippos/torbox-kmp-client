package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.UsenetDownloadListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface UsenetApi {
    suspend fun getUsenetList(
        bypassCache: String? = null,
        id: Int? = null,
        offset: Int? = null,
        limit: Int? = null,
    ): ApiResult<UsenetDownloadListResponseDto>
}
