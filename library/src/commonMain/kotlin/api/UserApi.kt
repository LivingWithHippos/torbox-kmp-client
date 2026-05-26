package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.UserDataResponseDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface UserApi {
    suspend fun refreshApiToken(sessionToken: String): ApiResult<GenericResponseDto>

    suspend fun getUserData(settings: Boolean? = null): ApiResult<UserDataResponseDto>
}
