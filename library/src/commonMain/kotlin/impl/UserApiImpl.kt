package com.github.livingwithhippos.torbox.kmp.client.impl

import com.github.livingwithhippos.torbox.kmp.client.api.UserApi
import com.github.livingwithhippos.torbox.kmp.client.model.AddSearchEngineRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.kmp.client.model.DeviceCodeAuthorizationStartDto
import com.github.livingwithhippos.torbox.kmp.client.model.DeviceCodeTokenDataDto
import com.github.livingwithhippos.torbox.kmp.client.model.DeviceCodeTokenRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.ModifySearchEngineDto
import com.github.livingwithhippos.torbox.kmp.client.model.ModifySearchEngineRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.ReferralDataDto
import com.github.livingwithhippos.torbox.kmp.client.model.RefreshApiTokenRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.SearchEngineControlRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.SearchEngineDto
import com.github.livingwithhippos.torbox.kmp.client.model.SubscriptionDto
import com.github.livingwithhippos.torbox.kmp.client.model.TransactionDto
import com.github.livingwithhippos.torbox.kmp.client.model.UserDataDto
import com.github.livingwithhippos.torbox.kmp.client.model.UserSettingsRequestDto
import com.github.livingwithhippos.torbox.kmp.client.model.UserStatsDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult
import com.github.livingwithhippos.torbox.kmp.client.network.executeRequest
import com.github.livingwithhippos.torbox.kmp.client.network.isSuccessOrRedirect
import com.github.livingwithhippos.torbox.kmp.client.network.noAuth
import com.github.livingwithhippos.torbox.kmp.client.network.safeBodyText
import com.github.livingwithhippos.torbox.kmp.client.network.toFailure
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class UserApiImpl(
    private val client: HttpClient,
) : UserApi {
    private val uuidRegex =
        Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")

    override suspend fun refreshToken(sessionToken: String): ApiResult<GenericResponseDto> {
        if (sessionToken.isBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("sessionToken must not be blank"),
            )
        }

        // todo: test this API to check the return type

        return executeRequest {
            client.post("user/refreshtoken") {
                contentType(ContentType.Application.Json)
                setBody(
                    RefreshApiTokenRequestDto(sessionToken = sessionToken),
                )
            }
        }
    }

    override suspend fun data(settings: Boolean?): ApiResult<ApiResponseDto<UserDataDto>> =
        executeRequest {
            client.get("user/me") {
                settings?.let { parameter("settings", it) }
            }
        }

    override suspend fun addReferral(
        referral: String,
        validate: Boolean,
    ): ApiResult<GenericResponseDto> {
        if (referral.isBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("referral must not be blank"),
            )
        }

        if (validate && !uuidRegex.matches(referral)) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("referral must be a valid UUID"),
            )
        }

        return executeRequest {
            client.post("user/addreferral") {
                parameter("referral", referral)
            }
        }
    }

    override suspend fun confirmationCode(): ApiResult<GenericResponseDto> =
        executeRequest {
            client.get("user/getconfirmation")
        }

    override suspend fun startAuthorizationProcess(app: String?): ApiResult<ApiResponseDto<DeviceCodeAuthorizationStartDto>> =
        executeRequest {
            client.get("user/auth/device/start") {
                noAuth()
                app?.trim()?.takeIf { it.isNotEmpty() }?.let { parameter("app", it) }
            }
        }

    override suspend fun token(deviceCode: String): ApiResult<ApiResponseDto<DeviceCodeTokenDataDto>> {
        if (deviceCode.isBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("deviceCode must not be blank"),
            )
        }

        return executeRequest {
            client.post("user/auth/device/token") {
                noAuth()
                contentType(ContentType.Application.Json)
                setBody(DeviceCodeTokenRequestDto(deviceCode = deviceCode))
            }
        }
    }

    override suspend fun referral(): ApiResult<ApiResponseDto<ReferralDataDto>> =
        executeRequest {
            client.get("user/referraldata")
        }

    override suspend fun subscriptions(): ApiResult<ApiResponseListDto<SubscriptionDto>> =
        executeRequest {
            client.get("user/subscriptions")
        }

    override suspend fun transactions(): ApiResult<ApiResponseListDto<TransactionDto>> =
        executeRequest {
            client.get("user/transactions")
        }

    override suspend fun transactionPdf(transactionId: String): ApiResult<ByteArray> {
        if (transactionId.isBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("transactionId must not be blank"),
            )
        }

        return runCatching {
            client.get("user/transaction/pdf") {
                parameter("transaction_id", transactionId)
            }
        }.fold(
            onSuccess = { response ->
                if (!response.status.isSuccessOrRedirect()) {
                    return@fold ApiResult.Failure.Http(response.status.value, response.safeBodyText())
                }

                runCatching { response.body<ByteArray>() }.fold(
                    onSuccess = { ApiResult.Success(it) },
                    // don't attempt to stringify binary payloads as error body
                    onFailure = { toFailure(it, null) },
                )
            },
            onFailure = { toFailure(it, null) },
        )
    }

    override suspend fun editSettings(settings: UserSettingsRequestDto): ApiResult<GenericResponseDto> =
        executeRequest {
            client.put("user/settings/editsettings") {
                contentType(ContentType.Application.Json)
                setBody(settings)
            }
        }

    override suspend fun searchEngines(id: Int?): ApiResult<ApiResponseListDto<SearchEngineDto>> =
        executeRequest {
            client.get("user/settings/searchengines") {
                id?.let { parameter("id", it) }
            }
        }

    override suspend fun controlSearchEngines(
        id: Int?,
        operation: String,
        all: Boolean?,
    ): ApiResult<GenericResponseDto> {
        val normalized = operation.trim().lowercase()
        if (normalized !in listOf("enable", "disable", "check", "delete")) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("operation must be one of: enable, disable, check, delete"),
            )
        } else if (id == null && all != true) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("Either an id or all = true must be provided"),
            )
        }

        return executeRequest {
            client.post("user/settings/controlsearchengines") {
                contentType(ContentType.Application.Json)
                setBody(SearchEngineControlRequestDto(operation = normalized, id = id, all = all))
            }
        }
    }

    override suspend fun modifySearchEngine(
        id: Int,
        type: String?,
        url: String?,
        apikey: String?,
        downloadType: String?,
    ): ApiResult<ApiResponseDto<ModifySearchEngineDto>> {
        if (id <= 0) {
            return ApiResult.Failure.BadRequest(IllegalArgumentException("id must be a positive integer"))
        }

        return executeRequest {
            client.post("user/settings/modifysearchengines") {
                contentType(ContentType.Application.Json)
                setBody(
                    ModifySearchEngineRequestDto(
                        id = id,
                        type = type,
                        url = url,
                        apikey = apikey,
                        downloadType = downloadType,
                    ),
                )
            }
        }
    }

    override suspend fun addSearchEngine(
        type: String?,
        url: String,
        apikey: String?,
        downloadType: String?,
    ): ApiResult<ApiResponseListDto<SearchEngineDto>> {
        if (url.isBlank()) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("url must not be blank"),
            )
        }

        val normalizedDownloadType = downloadType?.trim()?.lowercase()
        if (normalizedDownloadType != null && normalizedDownloadType !in listOf("torrents", "usenet")) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("downloadType must be one of: torrents, usenet"),
            )
        }

        return executeRequest {
            client.put("user/settings/addsearchengines") {
                contentType(ContentType.Application.Json)
                setBody(
                    AddSearchEngineRequestDto(
                        type = type,
                        url = url,
                        apikey = apikey,
                        downloadType = normalizedDownloadType,
                    ),
                )
            }
        }
    }

    override suspend fun stats(
        general: Boolean,
        bandwidth: Boolean,
        bandwidthGrouping: String?,
    ): ApiResult<ApiResponseDto<UserStatsDto>> {
        val normalizedGrouping = bandwidthGrouping?.trim()?.lowercase()
        if (normalizedGrouping != null && normalizedGrouping !in listOf("hour", "day", "week", "month")) {
            return ApiResult.Failure.BadRequest(
                IllegalArgumentException("bandwidthGrouping must be one of: hour, day, week, month"),
            )
        }

        return executeRequest {
            client.get("user/stats") {
                parameter("general", general)
                parameter("bandwidth", bandwidth)
                normalizedGrouping?.let { parameter("bandwidth_grouping", it) }
            }
        }
    }
}
