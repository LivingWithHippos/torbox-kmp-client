package com.github.livingwithhippos.torbox

import com.github.livingwithhippos.torbox.impl.GeneralApiImpl
import com.github.livingwithhippos.torbox.impl.UserApiImpl
import com.github.livingwithhippos.torbox.network.ApiResult
import com.github.livingwithhippos.torbox.network.TorboxAuthState
import com.github.livingwithhippos.torbox.network.TorboxClientConfig
import com.github.livingwithhippos.torbox.network.configureTorbox
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

/** Runs the API implementations against a mock engine with the real client setup. */
class MockApiTest {
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val requests = mutableListOf<HttpRequestData>()
    private val dispatcher = Dispatchers.Default

    private fun mockClient(
        body: String,
        authState: TorboxAuthState = TorboxAuthState("TOKEN"),
    ): HttpClient =
        HttpClient(
            MockEngine { request ->
                requests.add(request)
                respond(body, HttpStatusCode.OK, jsonHeaders)
            },
        ) {
            configureTorbox(TorboxClientConfig(), authState)
        }

    private val statsBody =
        """{"success":true,"error":null,"detail":"ok","data":{"total_users":10,"total_servers":2}}"""

    private val stringBody = """{"success":true,"error":null,"detail":"ok","data":"sent"}"""

    @Test
    fun noAuthRequestsSkipTheBearer() =
        runTest {
            val result = GeneralApiImpl(mockClient(statsBody), dispatcher).stats()

            assertIs<ApiResult.Success<*>>(result)
            val request = requests.single()
            assertEquals("https://api.torbox.app/v1/api/stats", request.url.toString())
            assertNull(request.headers[HttpHeaders.Authorization])
        }

    @Test
    fun normalRequestsSendTheBearer() =
        runTest {
            UserApiImpl(mockClient(stringBody), dispatcher).requestConfirmationCode()

            assertEquals("Bearer TOKEN", requests.single().headers[HttpHeaders.Authorization])
        }

    @Test
    fun updatedTokenIsUsed() =
        runTest {
            val authState = TorboxAuthState("OLD")
            val client = mockClient(stringBody, authState)
            authState.bearerToken = "NEW"
            UserApiImpl(client, dispatcher).requestConfirmationCode()

            assertEquals("Bearer NEW", requests.single().headers[HttpHeaders.Authorization])
        }

    @Test
    fun blankTokenSendsNoBearer() =
        runTest {
            UserApiImpl(mockClient(stringBody, TorboxAuthState("")), dispatcher).requestConfirmationCode()

            assertNull(requests.single().headers[HttpHeaders.Authorization])
        }

    @Test
    fun brokenJsonIsSerializationFailure() =
        runTest {
            val body = """{"success":true,"data":{"total_users":"not a number"}"""
            val result = GeneralApiImpl(mockClient(body), dispatcher).stats()

            assertIs<ApiResult.Failure.Serialization>(result)
            assertEquals(body, result.body)
        }
}
