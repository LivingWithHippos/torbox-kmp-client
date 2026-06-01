package com.github.livingwithhippos.torbox.kmp.client.impl

import com.github.livingwithhippos.torbox.kmp.client.api.IntegrationsApi
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.kmp.client.model.JobDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult
import com.github.livingwithhippos.torbox.kmp.client.network.executeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get

class IntegrationsApiImpl(
    private val client: HttpClient,
) : IntegrationsApi {
    override suspend fun jobs(hash: String?): ApiResult<ApiResponseListDto<JobDto>> {
        val url =
            when {
                hash.isNullOrBlank().not() -> "integration/jobs/$hash"
                else -> "integration/jobs"
            }

        return executeRequest {
            client.get(url)
        }
    }

    override suspend fun job(id: Int): ApiResult<ApiResponseDto<JobDto>> =
        executeRequest {
            client.get("integration/job/$id")
        }

    override suspend fun delete(id: Int): ApiResult<ApiResponseDto<String>> =
        executeRequest {
            client.delete("integration/job/$id")
        }
}
