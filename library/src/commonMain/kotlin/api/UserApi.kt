package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.GenericStringResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.UserDataResponseDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface UserApi {
    suspend fun refreshApiToken(sessionToken: String): ApiResult<GenericStringResponseDto>

    suspend fun getUserData(settings: Boolean? = null): ApiResult<UserDataResponseDto>
}
