package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.StatsResponseDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface GeneralApi {
    suspend fun getUpStatus(): ApiResult<GenericResponseDto>

    suspend fun getStats(): ApiResult<StatsResponseDto>
}
