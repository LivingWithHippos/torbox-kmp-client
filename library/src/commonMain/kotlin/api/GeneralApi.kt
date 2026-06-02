package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.ApiResponseListDto
import com.github.livingwithhippos.torbox.kmp.client.model.ChangelogEntryDto
import com.github.livingwithhippos.torbox.kmp.client.model.SpeedtestFileDto
import com.github.livingwithhippos.torbox.kmp.client.model.StatsDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface GeneralApi {
    /**
     * Simply gets if the TorBox API is available for use or not. Also, might include information about downtimes.
     *
     * [online documentation](https://api-docs.torbox.app/#2781b110-d116-4e17-92d9-4146149aad95)
     */
    suspend fun upStatus(): ApiResult<ApiResponseDto<String>>

    /**
     * Simply gets general stats about the TorBox service.
     *
     * [online documentation](https://api-docs.torbox.app/#8ab7b908-f1d0-48ff-ad6c-77b6c65ccaf6)
     */
    suspend fun stats(): ApiResult<ApiResponseDto<StatsDto>>

    /**
     * Gets most recent 100 changelogs from [https://feedback.torbox.app/changelog](https://feedback.torbox.app/changelog).
     * This is useful for people who want updates on the TorBox changelog.
     * Includes all the necessary items to make this compatible with RSS feed viewers for notifications, and proper HTML viewing.
     *
     * [online documentation](https://api-docs.torbox.app/#5a13f7e5-1166-4758-9d67-acac18ed3bac)
     *
     * The XML is returned, unparsed, as a string
     */
    suspend fun changelogRSSFeed(): ApiResult<String>

    /**
     * Gets most recent 100 changelogs from [https://feedback.torbox.app/changelog](https://feedback.torbox.app/changelog).
     * This is useful for people who want updates on the TorBox changelog.
     * Includes all the necessary items to make this compatible with RSS feed viewers for notifications, and proper HTML viewing.
     *
     * [online documentation](https://api-docs.torbox.app/#93e8360e-5050-4fd8-aec8-412ead2d5392)
     */
    suspend fun changelogJson(): ApiResult<ApiResponseListDto<ChangelogEntryDto>>

    /**
     * Gets CDN speedtest files.
     * This can be used for speed testing TorBox for users or other usages, such as checking download speeds for verification.
     * Provides all necessary data such as region, server name, and even coordinates.
     * Uses the requesting IP to determine if the server is the closest to the user, or [userIp] to specify a custom one.
     *
     * You also have the ability to choose between long tests or short tests via the [testLength] parameter.
     * You may also force the region selection by passing the [region] as a specific region.
     *
     * [online documentation](https://api-docs.torbox.app/#ac283d81-53d8-414c-9f53-01c8552bb9e7)
     */
    suspend fun speedtestFiles(
        testLength: String? = null,
        region: String? = null,
        userIp: String? = null,
    ): ApiResult<ApiResponseListDto<SpeedtestFileDto>>
}