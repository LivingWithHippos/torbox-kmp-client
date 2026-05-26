package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.DeviceCodeAuthorizationStartResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.DeviceCodeTokenResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.ReferralDataResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.SubscriptionResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.TransactionResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.UserDataResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.UserSearchEnginesResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.UserSettingsRequestDto
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

    /**
     * Automatically adds a referral code to the user's account.
     *
     * [online documentation](https://api-docs.torbox.app/#703cd861-908a-4021-b682-9414f581c9fa)
     *
     * @param referral Referral code in UUID format.
     * @param validate A regex will be run to check the referral UUID
     */
    suspend fun addReferralToAccount(
        referral: String,
        validate: Boolean = false,
    ): ApiResult<GenericResponseDto>

    /**
     * Requests a 6-digit code to be sent to the user's email for verification.
     *
     * [online documentation](https://api-docs.torbox.app/#1c1bfac4-bf2f-4fad-9ae8-a12275be3bd1)
     */
    suspend fun getConfirmationCode(): ApiResult<GenericResponseDto>

    /**
     * Starts the OAuth device code authorization process.
     *
     * [online documentation](https://api-docs.torbox.app/#744bc274-88b7-4faf-a833-e30573c8c1e0)
     *
     * @param app App name shown on the verification page, defaults to "Third Party App"
     */
    suspend fun startDeviceCodeAuthorizationProcess(app: String?): ApiResult<DeviceCodeAuthorizationStartResponseDto>

    /**
     * Redeems a token from the OAuth device code authorization process.
     *
     * [online documentation](https://api-docs.torbox.app/#6169f2b1-85e4-4990-9ce1-f4be25bd50c8)
     *
     * @param deviceCode The `device_code` returned by startDeviceCodeAuthorizationProcess.
     */
    suspend fun getTokenFromDeviceCodeProcess(deviceCode: String): ApiResult<DeviceCodeTokenResponseDto>

    /**
     * Gets the user's referral statistics and referral code.
     *
     * [online documentation](https://api-docs.torbox.app/#eb5bd5bd-0d7b-4014-bf2b-497a87cc6a97)
     */
    suspend fun getReferral(): ApiResult<ReferralDataResponseDto>

    /**
     * Gets the user's subscriptions, including active and inactive subscriptions.
     *
     * [online documentation](https://api-docs.torbox.app/#bb1307f2-ce3b-4625-a5be-e6ea1ab46a61)
     */
    suspend fun getSubscriptions(): ApiResult<SubscriptionResponseDto>

    /**
     * Gets the user's list of transactions.
     *
     * [online documentation](https://api-docs.torbox.app/#4205b949-fa83-46e8-8efb-dfa35187b890)
     */
    suspend fun getTransactions(): ApiResult<TransactionResponseDto>

    /**
     * Generates a PDF of the transaction information. Requires the transaction ID from the "Get Transactions" request.
     *
     * [online documentation](https://api-docs.torbox.app/#a55ecc56-3844-4874-a343-177c49d80773)
     *
     * @param transactionId The transaction ID that you want to generate the PDF for.
     */
    suspend fun getTransactionPdf(transactionId: String): ApiResult<ByteArray>

    /**
     * Edits a user's TorBox settings. Values take effect immediately.
     *
     * The request body must match the API's accepted settings keys. See the API docs for allowed
     * options — this method accepts a typed [UserSettingsRequestDto] so callers may supply only
     * the fields they want to change.
     *
     * [online documentation](https://api-docs.torbox.app/#263427ea-0129-4f59-a0e4-11ef3e9364f4)
     *
     * @param settings A [UserSettingsRequestDto] containing the settings to update.
     */
    suspend fun editSettings(settings: UserSettingsRequestDto): ApiResult<GenericResponseDto>

    /**
     * Gets all of user's configured search engines. Can pass an optional id to retrieve a specific search engine.
     *
     * [online documentation](https://api-docs.torbox.app/#fbb2cd77-7dbf-4e73-913c-8544f7acedd0)
     *
     * @param id Optional search engine id to retrieve a specific engine owned by the user.
     */
    suspend fun getSearchEngines(id: Int? = null): ApiResult<UserSearchEnginesResponseDto>

    /**
     * Controls a user's search engine(s). Allows operations: enable, disable, check, delete.
     *
     * [online documentation](https://api-docs.torbox.app/#828c7226-daad-4737-8ed5-98c9d5fa1089)
     *
     * @param id Optional ID of a specific search engine to operate on.
     * @param operation One of: enable, disable, check, delete.
     * @param all If true, perform operation on all user's search engines.
     */
    suspend fun controlSearchEngines(
        id: Int?,
        operation: String,
        all: Boolean?,
    ): ApiResult<GenericResponseDto>
}
