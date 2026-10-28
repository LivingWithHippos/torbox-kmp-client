package com.github.livingwithhippos.torbox.impl

import com.github.livingwithhippos.torbox.api.IntegrationsApi
import com.github.livingwithhippos.torbox.model.ApiResponseDto
import com.github.livingwithhippos.torbox.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.model.JobDto
import com.github.livingwithhippos.torbox.network.ApiResult
import com.github.livingwithhippos.torbox.network.executeRequest
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import io.ktor.client.request.delete
import io.ktor.client.request.get

class IntegrationsApiImpl(
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher,
) : IntegrationsApi {
    override suspend fun jobs(hash: String?): ApiResult<ApiResponseListDto<JobDto>> {
        val url =
            when {
                hash.isNullOrBlank().not() -> "integration/jobs/$hash"
                else -> "integration/jobs"
            }

        return executeRequest(dispatcher) {
            client.get(url)
        }
    }

    override suspend fun job(id: Int): ApiResult<ApiResponseDto<JobDto>> =
        executeRequest(dispatcher) {
            client.get("integration/job/$id")
        }

    override suspend fun delete(id: Int): ApiResult<ApiResponseDto<String>> =
        executeRequest(dispatcher) {
            client.delete("integration/job/$id")
        }
}
