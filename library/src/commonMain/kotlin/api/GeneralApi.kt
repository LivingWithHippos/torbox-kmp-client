package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.kmp.client.model.ChangelogEntryDto
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.SpeedtestFileDto
import com.github.livingwithhippos.torbox.kmp.client.model.StatsDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface GeneralApi {
    suspend fun getUpStatus(): ApiResult<GenericResponseDto>

    suspend fun getStats(): ApiResult<ApiResponseDto<StatsDto>>

    suspend fun getChangelog(): ApiResult<String>

    suspend fun getChangelogJson(): ApiResult<ApiResponseListDto<ChangelogEntryDto>>

    suspend fun getSpeedtestFiles(
        testLength: String? = null,
        region: String? = null,
        userIp: String? = null,
    ): ApiResult<ApiResponseListDto<SpeedtestFileDto>>
}
