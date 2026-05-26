package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.UserDataResponseDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface UserApi {
    /**
     * If you want a new API token, or your old one has been compromised, you may request a new one.
     * If you happen to forget the token, go the [TorBox settings page](https://torbox.app/settings) and copy the current one.
     * If this still doesn't work, you may contact us at our support email for a new one.
     *
     * [online documentation](https://api-docs.torbox.app/#43f61fca-261a-44b9-851e-9633d8a027aa)
     *
     * @param sessionToken You can find the session_token in the localStorage of your browser on any TorBox.app page under the key torbox_session_token.
     *  This is a temporary token that only lasts for 1 hour, which is why it is used here to verify the identity of a user as well as their API token
     */
    suspend fun refreshApiToken(sessionToken: String): ApiResult<GenericResponseDto>

    /**
     * Gets a users account data and informatiom
     *
     * [online documentation](https://api-docs.torbox.app/#a3775d9b-f3ee-49cd-98a4-fc909ed2923b)
     *
     * @param settings Allows you to retrieve user settings. Null defaults to true (default)
     */
    suspend fun getUserData(settings: Boolean? = null): ApiResult<UserDataResponseDto>
}
