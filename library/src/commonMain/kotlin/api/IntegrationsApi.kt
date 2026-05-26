package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.JobsResponseDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface IntegrationsApi {
    // note: this is one of the few changes from the original APIs

    /**
     * Gets a list of all jobs, a single job if specified by id, or a list of jobs with a specific hash
     *
     * [online documentation for all jobs](https://api-docs.torbox.app/#76b327e6-8bfd-4c45-a99b-0cee435f084)
     *
     * [online documentation for id](https://api-docs.torbox.app/#07a30808-e06d-4e87-a38f-2595a9ae2bc4)
     *
     * [online documentation for hash](https://api-docs.torbox.app/#9c3f2993-efcc-46a2-8d39-da5e774f8b5b)
     */
    suspend fun getJobs(
        id: Int? = null,
        hash: String? = null,
    ): ApiResult<JobsResponseDto>

    /**
     *
     * [online documentation](https://api-docs.torbox.app/#50d0f0fa-f189-4883-9068-42a6937b59c6)
     */
    suspend fun deleteJob(id: Int): ApiResult<GenericResponseDto>
}
