package com.github.livingwithhippos.torbox.kmp.client.api

import com.github.livingwithhippos.torbox.kmp.client.model.GenericResponseDto
import com.github.livingwithhippos.torbox.kmp.client.model.NotificationsResponseDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult

interface NotificationsApi {
    suspend fun getNotificationsRss(token: String): ApiResult<String>

    suspend fun getNotifications(): ApiResult<NotificationsResponseDto>

    suspend fun clearNotifications(): ApiResult<GenericResponseDto>

    suspend fun clearNotification(notificationId: Int): ApiResult<GenericResponseDto>

    suspend fun sendTestNotification(): ApiResult<GenericResponseDto>
}
