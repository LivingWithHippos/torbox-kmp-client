package com.github.livingwithhippos.torbox.impl

import com.github.livingwithhippos.torbox.api.GeneralApi
import com.github.livingwithhippos.torbox.model.ApiResponseDto
import com.github.livingwithhippos.torbox.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.model.ChangelogEntryDto
import com.github.livingwithhippos.torbox.model.SpeedtestFileDto
import com.github.livingwithhippos.torbox.model.StatsDto
import com.github.livingwithhippos.torbox.network.ApiResult
import com.github.livingwithhippos.torbox.network.executeRequest
import com.github.livingwithhippos.torbox.network.noAuth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

class GeneralApiImpl(
    private val client: HttpClient,
) : GeneralApi {
    override suspend fun upStatus(): ApiResult<ApiResponseDto<String>> =
        executeRequest {
            // what happens if the api are failing? does this return success false?
            client.get("https://api.torbox.app") {
                noAuth()
            }
        }

    override suspend fun stats(): ApiResult<ApiResponseDto<StatsDto>> =
        executeRequest {
            client.get("stats") {
                noAuth()
            }
        }

    override suspend fun changelogRSSFeed(): ApiResult<String> =
        runCatching {
            val response =
                client.get("changelogs/rss") {
                    noAuth()
                }
            if (response.status.isSuccess()) {
                ApiResult.Success(response.body<String>())
            } else {
                ApiResult.Failure.Http(response.status.value, response.bodyAsText())
            }
        }.getOrElse {
            ApiResult.Failure.Network(it)
        }

    override suspend fun changelogJson(): ApiResult<ApiResponseListDto<ChangelogEntryDto>> =
        executeRequest {
            client.get("changelogs/json") {
                noAuth()
            }
        }

    override suspend fun speedtestFiles(
        testLength: String?,
        region: String?,
        userIp: String?,
    ): ApiResult<ApiResponseListDto<SpeedtestFileDto>> {
        val normalizedTestLength = testLength?.trim()?.lowercase()
        if (normalizedTestLength != null && normalizedTestLength !in listOf("short", "long")) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("testLength must be one of: short, long"),
            )
        }

        return executeRequest {
            client.get("speedtest") {
                noAuth()
                parameter("test_length", normalizedTestLength)
                region?.takeIf { it.isNotBlank() }?.let { parameter("region", it) }
                userIp?.takeIf { it.isNotBlank() }?.let { parameter("user_ip", it) }
            }
        }
    }
}
