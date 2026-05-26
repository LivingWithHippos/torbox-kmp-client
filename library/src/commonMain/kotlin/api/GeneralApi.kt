package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.ChangelogListResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.SpeedtestFilesResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.StatsResponseDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface GeneralApi {
    suspend fun getUpStatus(): ApiResult<GenericResponseDto>

    suspend fun getStats(): ApiResult<StatsResponseDto>

    suspend fun getChangelog(): ApiResult<String>

    suspend fun getChangelogJson(): ApiResult<ChangelogListResponseDto>

    suspend fun getSpeedtestFiles(
        testLength: String? = null,
        region: String? = null,
        userIp: String? = null,
    ): ApiResult<SpeedtestFilesResponseDto>
}
