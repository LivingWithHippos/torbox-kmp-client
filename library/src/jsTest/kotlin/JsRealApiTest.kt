package com.github.livingwithhippos.torbox

import com.github.livingwithhippos.torbox.client.TorboxClient
import com.github.livingwithhippos.torbox.network.ApiResult
import com.github.livingwithhippos.torbox.network.TorboxAuthState
import com.github.livingwithhippos.torbox.network.TorboxClientConfig
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * The @Test annotation has been placed on core functions (such as torrentList and upStatus).
 * Additional tests mirror JvmRealApiTest. Populate mise.local.toml or local.properties and add @Test
 * to test specific real responses on Kotlin/JS.
 */
class JsRealApiTest {
    private val client =
        TorboxClient.create(
            TorboxClientConfig(logLevel = "info"),
            TorboxAuthState(JsTestConfig.bearerToken),
        )

    /***********
     * TORRENT *
     **********/

    fun torrentAsyncCreate() =
        runTest {
            val response = client.torrent.asyncCreate(magnet = JsTestConfig.magnetLink)
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun torrentCreate() =
        runTest {
            val response = client.torrent.create(magnet = JsTestConfig.magnetLink)
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun torrentControl() =
        runTest {
            val response = client.torrent.control(torrentId = JsTestConfig.torrentId, operation = "delete")
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun torrentEdit() =
        runTest {
            val response = client.torrent.edit(torrentId = JsTestConfig.torrentId, tags = listOf("Test"))
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun torrentList() =
        runTest {
            val response = client.torrent.list()
            assertTrue { response is ApiResult.Success && response.value.success }
            println(response)
        }

    fun torrentGet() =
        runTest {
            val response = client.torrent.get(id = JsTestConfig.torrentId)
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun torrentDownloadLink() =
        runTest {
            val response =
                client.torrent.downloadLink(
                    token = JsTestConfig.bearerToken,
                    torrentId = JsTestConfig.torrentId,
                )
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun torrentListAvailability() =
        runTest {
            val response = client.torrent.listAvailability(hashes = listOf(JsTestConfig.torrentHash), listFiles = true)
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun torrentListAvailabilityObject() =
        runTest {
            val response =
                client.torrent.listAvailabilityObject(hashes = listOf(JsTestConfig.torrentHash), listFiles = false)
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun torrentListAvailabilityBatchObject() =
        runTest {
            val response = client.torrent.listAvailabilityBatchObject(hashes = listOf(JsTestConfig.torrentHash))
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun torrentListAvailabilityBatch() =
        runTest {
            val response = client.torrent.listAvailabilityBatch(hashes = JsTestConfig.torrentHash.split(","))
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun torrentExportMagnet() =
        runTest {
            val response = client.torrent.export(torrentId = JsTestConfig.torrentId, type = "file")
            assertTrue { response is ApiResult.Success }
        }

    fun torrentGetInfo() =
        runTest {
            val response = client.torrent.info(hash = JsTestConfig.magnetHash)
            assertTrue { response is ApiResult.Success }
        }

    fun torrentGetFullInfo() =
        runTest {
            val response = client.torrent.fullInfo(magnet = JsTestConfig.magnetLink, timeout = 15)
            assertTrue { response is ApiResult.Success }
        }

    fun torrentMagnetToFile() =
        runTest {
            val response = client.torrent.magnetToFile(magnet = JsTestConfig.magnetLink)
            assertTrue { response is ApiResult.Success }
        }

    /***********
     * GENERAL *
     **********/

    @Test
    fun upStatus() =
        runTest {
            val response = client.general.upStatus()
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun stats() =
        runTest {
            val response = client.general.stats()
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun changelogRssFeed() =
        runTest {
            val response = client.general.changelogRSSFeed()
            assertTrue { response is ApiResult.Success }
        }

    fun changelogJsonFeed() =
        runTest {
            val response = client.general.changelogJson()
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun speedTestFiles() =
        runTest {
            val response = client.general.speedtestFiles()
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    /*******
     * WEB *
     *******/

    fun webCreate() =
        runTest {
            val response = client.web.create(link = JsTestConfig.hosterLink)
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun webControl() =
        runTest {
            val response = client.web.control(webId = JsTestConfig.webDownloadId, operation = "delete")
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun webList() =
        runTest {
            val response = client.web.list()
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun webGet() =
        runTest {
            val response = client.web.get(id = JsTestConfig.webDownloadId)
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun webDownloadLink() =
        runTest {
            val response = client.web.downloadLink(token = JsTestConfig.bearerToken, webId = JsTestConfig.webDownloadId)
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun webListAvailability() =
        runTest {
            val response = client.web.listAvailability(hashes = listOf(JsTestConfig.webHash))
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun webListAvailabilityObject() =
        runTest {
            val response = client.web.listAvailabilityObject(hashes = listOf(JsTestConfig.webHash))
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun webListAvailabilityBatchObject() =
        runTest {
            val response = client.web.listAvailabilityBatchObject(hashes = listOf(JsTestConfig.webHash))
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun webListAvailabilityBatch() =
        runTest {
            val response = client.web.listAvailabilityBatch(hashes = listOf(JsTestConfig.webHash))
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun webHosters() =
        runTest {
            val response = client.web.hosters()
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun webEdit() =
        runTest {
            val response = client.web.edit(webId = JsTestConfig.webDownloadId, tags = listOf("Test"))
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    /*****************
     * NOTIFICATIONS *
     ****************/

    fun notificationsRssFeed() =
        runTest {
            val response = client.notifications.rssFeed(JsTestConfig.bearerToken)
            assertTrue { response is ApiResult.Success }
        }

    fun notificationsJsonFeed() =
        runTest {
            val response = client.notifications.jsonFeed()
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun clearNotifications() =
        runTest {
            val response = client.notifications.clear(null)
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun sendTestNotification() =
        runTest {
            val response = client.notifications.sendTest()
            assertTrue { response is ApiResult.Success && response.value.success }
        }
}
