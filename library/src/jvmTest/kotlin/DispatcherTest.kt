package com.github.livingwithhippos.torbox

import com.github.livingwithhippos.torbox.impl.TorrentApiImpl
import com.github.livingwithhippos.torbox.network.ApiResult
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.statement.HttpResponsePipeline
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import kotlin.test.Test
import kotlin.test.assertTrue

/** Checks that responses are decoded on the configured dispatcher, not the caller's thread. */
class DispatcherTest {
    @Test
    fun `response is decoded on the configured dispatcher`() {
        val decodeDispatcher =
            Executors
                .newSingleThreadExecutor { Thread(it, "torbox-decode") }
                .asCoroutineDispatcher()
        val engine =
            MockEngine {
                respond(
                    content = """{"success":true,"error":null,"detail":"ok","data":[]}""",
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
        val client = HttpClient(engine) { install(ContentNegotiation) { json() } }

        var decodeThread: String? = null
        // runs where the body is transformed (i.e. where JSON decoding happens)
        client.responsePipeline.intercept(HttpResponsePipeline.Transform) {
            decodeThread = Thread.currentThread().name
            proceed()
        }

        decodeDispatcher.use { dispatcher ->
            val callerThread = Thread.currentThread().name
            val result = runBlocking { TorrentApiImpl(client, dispatcher).list(null, null, null) }

            assertTrue(result is ApiResult.Success)
            // coroutine debug mode may append " @coroutine#N" to the thread name
            assertTrue(decodeThread!!.startsWith("torbox-decode"), "decoded on $decodeThread")
            assertTrue(!decodeThread!!.startsWith(callerThread))
        }
    }
}
