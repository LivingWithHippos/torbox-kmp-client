package com.github.livingwithhippos.torbox.kmp.client.impl

import com.github.livingwithhippos.torbox.kmp.client.api.IntegrationsApi
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.JobResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.JobsResponseDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult
import com.github.livingwithhippos.torbox.kmp.client.network.executeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get

class IntegrationsApiImpl(
    private val client: HttpClient,
) : IntegrationsApi {
    override suspend fun jobs(hash: String?): ApiResult<JobsResponseDto> {
        val url =
            when {
                hash.isNullOrBlank().not() -> "integration/jobs/$hash"
                else -> "integration/jobs"
            }

        return executeRequest {
            client.get(url)
        }
    }

    override suspend fun job(id: Int): ApiResult<JobResponseDto> =
        executeRequest {
            client.get("integration/job/$id")
        }

    override suspend fun delete(id: Int): ApiResult<GenericResponseDto> =
        executeRequest {
            client.delete("integration/job/$id")
        }
}
