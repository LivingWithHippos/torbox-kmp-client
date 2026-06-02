package com.github.livingwithhippos.torbox.api

import com.github.livingwithhippos.torbox.model.ApiResponseDto
import com.github.livingwithhippos.torbox.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.model.JobDto
import com.github.livingwithhippos.torbox.network.ApiResult

interface IntegrationsApi {
    /**
     * Gets all the jobs attached to a user account.
     * This is good for an overall view of the jobs, such as on a dashboard, or something similar.
     *
     * Optionally filter jobs by hash.
     * Good for checking on specific downloads such as a download page, that could contain a lot of jobs.
     *
     * [online documentation for all jobs](https://api-docs.torbox.app/#76b327e6-8bfd-4c45-a99b-0cee435f084f)
     *
     * [online documentation for hash](https://api-docs.torbox.app/#9c3f2993-efcc-46a2-8d39-da5e774f8b5b)
     *
     * These two APIs have been merged into one.
     */
    suspend fun jobs(hash: String? = null): ApiResult<ApiResponseListDto<JobDto>>

    /**
     * Gets a specific job using the Job's ID. To get the ID, you will have to Get All Jobs, and get the ID you want.
     *
     * [online documentation](https://api-docs.torbox.app/#07a30808-e06d-4e87-a38f-2595a9ae2bc4)
     */
    suspend fun job(id: Int): ApiResult<ApiResponseDto<JobDto>>

    /**
     * Cancels a job or deletes the job. Cancels while in progress (pending, uploading), or deletes the job any other time.
     * It will delete it from the database completely.
     *
     * [online documentation](https://api-docs.torbox.app/#50d0f0fa-f189-4883-9068-42a6937b59c6)
     */
    suspend fun delete(id: Int): ApiResult<ApiResponseDto<String>>
}
