package com.github.livingwithhippos.torbox.api

import com.github.livingwithhippos.torbox.model.ApiResponseDto
import com.github.livingwithhippos.torbox.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.model.DashboardFilterDto
import com.github.livingwithhippos.torbox.model.DeviceCodeAuthorizationStartDto
import com.github.livingwithhippos.torbox.model.DeviceCodeTokenDataDto
import com.github.livingwithhippos.torbox.model.ModifySearchEngineDto
import com.github.livingwithhippos.torbox.model.ReferralDataDto
import com.github.livingwithhippos.torbox.model.SearchEngineDto
import com.github.livingwithhippos.torbox.model.SubscriptionDto
import com.github.livingwithhippos.torbox.model.TransactionDto
import com.github.livingwithhippos.torbox.model.UserDataDto
import com.github.livingwithhippos.torbox.model.UserSettingsRequestDto
import com.github.livingwithhippos.torbox.model.UserStatsDto
import com.github.livingwithhippos.torbox.network.ApiResult

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
    suspend fun refreshToken(sessionToken: String): ApiResult<ApiResponseDto<String>>

    /**
     * Gets a users account data and information
     *
     * [online documentation](https://api-docs.torbox.app/#a3775d9b-f3ee-49cd-98a4-fc909ed2923b)
     *
     * @param settings Allows you to retrieve user settings. Null defaults to true (default)
     */
    suspend fun data(settings: Boolean? = null): ApiResult<ApiResponseDto<UserDataDto>>

    /**
     * Automatically adds a referral code to the user's account.
     *
     * [online documentation](https://api-docs.torbox.app/#703cd861-908a-4021-b682-9414f581c9fa)
     *
     * @param referral Referral code in UUID format.
     * @param validate A regex will be run to check the referral UUID
     */
    suspend fun addReferral(
        referral: String,
        validate: Boolean = false,
    ): ApiResult<ApiResponseDto<String>>

    /**
     * Requests a 6-digit code to be sent to the user's email for verification.
     *
     * [online documentation](https://api-docs.torbox.app/#1c1bfac4-bf2f-4fad-9ae8-a12275be3bd1)
     */
    suspend fun requestConfirmationCode(): ApiResult<ApiResponseDto<String>>

    /**
     * Starts the OAuth device code authorization process.
     *
     * [online documentation](https://api-docs.torbox.app/#744bc274-88b7-4faf-a833-e30573c8c1e0)
     *
     * @param app App name shown on the verification page, defaults to "Third Party App"
     */
    suspend fun startAuthorizationProcess(app: String?): ApiResult<ApiResponseDto<DeviceCodeAuthorizationStartDto>>

    /**
     * Redeems a token from the OAuth device code authorization process.
     *
     * [online documentation](https://api-docs.torbox.app/#6169f2b1-85e4-4990-9ce1-f4be25bd50c8)
     *
     * @param deviceCode The `device_code` returned by startDeviceCodeAuthorizationProcess.
     */
    suspend fun token(deviceCode: String): ApiResult<ApiResponseDto<DeviceCodeTokenDataDto>>

    /**
     * Gets the user's referral statistics and referral code.
     *
     * [online documentation](https://api-docs.torbox.app/#eb5bd5bd-0d7b-4014-bf2b-497a87cc6a97)
     */
    suspend fun referral(): ApiResult<ApiResponseDto<ReferralDataDto>>

    /**
     * Gets the user's subscriptions, including active and inactive subscriptions.
     *
     * [online documentation](https://api-docs.torbox.app/#bb1307f2-ce3b-4625-a5be-e6ea1ab46a61)
     */
    suspend fun subscriptions(): ApiResult<ApiResponseListDto<SubscriptionDto>>

    /**
     * Gets the user's list of transactions.
     *
     * [online documentation](https://api-docs.torbox.app/#4205b949-fa83-46e8-8efb-dfa35187b890)
     */
    suspend fun transactions(): ApiResult<ApiResponseListDto<TransactionDto>>

    /**
     * Generates a PDF of the transaction information. Requires the transaction ID from the "Get Transactions" request.
     *
     * [online documentation](https://api-docs.torbox.app/#a55ecc56-3844-4874-a343-177c49d80773)
     *
     * @param transactionId The transaction ID that you want to generate the PDF for.
     */
    suspend fun transactionPdf(transactionId: String): ApiResult<ByteArray>

    /**
     * Edits a user's TorBox settings. Values take effect immediately.
     *
     * The request body must match the API's accepted settings keys. See the API docs for allowed
     * options — this method accepts a typed [UserSettingsRequestDto] so callers may supply only
     * the fields they want to change.
     *
     * [online documentation](https://api-docs.torbox.app/#263427ea-0129-4f59-a0e4-11ef3e9364f4)
     *
     */
    suspend fun edit(
        emailNotifications: Boolean? = null,
        webNotifications: Boolean? = null,
        mobileNotifications: Boolean? = null,
        rssNotifications: Boolean? = null,
        downloadSpeedInTab: Boolean? = null,
        showTrackerInTorrents: Boolean? = null,
        stremioQuality: List<Int>? = null,
        stremioResolution: List<Int>? = null,
        stremioLanguage: List<Int>? = null,
        stremioCache: List<Int>? = null,
        stremioSizeLower: Long? = null,
        googleDriveFolderId: String? = null,
        onedriveSavePath: String? = null,
        discordId: String? = null,
        discordNotifications: Boolean? = null,
        stremioAllowAdult: Boolean? = null,
        stremioSeedTorrents: Int? = null,
        seedTorrents: Int? = null,
        allowZipped: Boolean? = null,
        stremioAllowZipped: Boolean? = null,
        onefichierFolderId: String? = null,
        gofileFolderId: String? = null,
        jdownloaderNotifications: Boolean? = null,
        webhookNotifications: Boolean? = null,
        webhookUrl: String? = null,
        telegramNotifications: Boolean? = null,
        telegramId: String? = null,
        megaEmail: String? = null,
        megaPassword: String? = null,
        stremioSort: String? = null,
        cdnSelection: String? = null,
        stremioUseCustomSearchEngines: Boolean? = null,
        stremioResultSort: String? = null,
        webdavUseLocalFiles: Boolean? = null,
        stremioLegacyYourMedia: Boolean? = null,
        stremioOnlyYourMediaStreams: Boolean? = null,
        stremioDisableYourMediaStreams: Boolean? = null,
        webdavUseFolderView: Boolean? = null,
        webdavFlatten: Boolean? = null,
        stremioLimitPerResolutionTorrent: Int? = null,
        stremioLimitPerResolutionUsenet: Int? = null,
        stremioSizeUpper: Long? = null,
        stremioTorrentSeedersCutoff: Int? = null,
        pixeldrainApiKey: String? = null,
        onefichierApiKey: String? = null,
        gofileApiKey: String? = null,
        patreonId: String? = null,
        stremioWaitForDownloadUsenet: Boolean? = null,
        stremioWaitForDownloadTorrent: Boolean? = null,
        stremioDisableFilteredNote: Boolean? = null,
        stremioEmojiInDescription: Boolean? = null,
        // todo: decide if we should also split this into its arguments
        dashboardFilter: DashboardFilterDto? = null,
        webPlayerAlwaysTranscode: Boolean? = null,
        webPlayerAlwaysSkipIntro: Boolean? = null,
        webPlayerAudioPreferredLanguage: String? = null,
        webPlayerSubtitlePreferredLanguage: String? = null,
        webPlayerDisablePrestreamSelector: Boolean? = null,
        dashboardSort: String? = null,
        webPlayerDisableNextUpDialogue: Boolean? = null,
        webPlayerEnableScrobbling: Boolean? = null,
        appendFilenameToLinks: Boolean? = null,
    ): ApiResult<ApiResponseDto<String>>

    /**
     * Gets all of user's configured search engines. Can pass an optional id to retrieve a specific search engine.
     *
     * [online documentation](https://api-docs.torbox.app/#fbb2cd77-7dbf-4e73-913c-8544f7acedd0)
     *
     * @param id Optional search engine id to retrieve a specific engine owned by the user.
     */
    suspend fun searchEngines(id: Int? = null): ApiResult<ApiResponseListDto<SearchEngineDto>>

    /**
     * Gets a user's stats. Use the query params to select general and/or bandwidth data.
     *
     * [online documentation](https://api-docs.torbox.app/#1eed7757-6c80-4fce-97d2-1d1ad2953dd6)
     */
    suspend fun stats(
        general: Boolean = true,
        bandwidth: Boolean = false,
        bandwidthGrouping: String? = null,
    ): ApiResult<ApiResponseDto<UserStatsDto>>

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
    ): ApiResult<ApiResponseDto<String>>

    /**
     * Edits a user's search engine.
     *
     * [online documentation](https://api-docs.torbox.app/#8979611e-5dae-4ee0-81dd-e1bf41570660)
     */
    suspend fun modifySearchEngine(
        id: Int,
        type: String? = null,
        url: String? = null,
        apikey: String? = null,
        downloadType: String? = null,
    ): ApiResult<ApiResponseDto<ModifySearchEngineDto>>

    /**
     * Adds a new search engine to the user's account.
     *
     * [online documentation](https://api-docs.torbox.app/#8a1c54fa-8485-4fc0-8b42-17cddd082967)
     *
     * @param type Search engine type (prowlarr, jackett, nzbhydra). Optional.
     * @param url Search engine URL (http/https).
     * @param apikey API key for the search engine (optional).
     * @param downloadType Download type (torrents, usenet). Optional.
     */
    suspend fun addSearchEngine(
        type: String? = null,
        url: String,
        apikey: String? = null,
        downloadType: String? = null,
    ): ApiResult<ApiResponseListDto<SearchEngineDto>>
}
