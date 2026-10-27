package com.github.livingwithhippos.torbox

import com.github.livingwithhippos.torbox.client.TorboxClient
import com.github.livingwithhippos.torbox.network.ApiResult
import com.github.livingwithhippos.torbox.network.TorboxAuthState
import com.github.livingwithhippos.torbox.network.TorboxClientConfig
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * The @Test has been removed from the functions to avoid triggering it with the Gradle tasks,
 * populate the local properties file like the example and readd them to test real responses
 */
class JvmRealApiTest {
    private val client =
        TorboxClient.create(TorboxClientConfig(logLevel = "info"), TorboxAuthState(TestConfig.bearerToken))

    /***********
     * TORRENT *
     **********/

    fun `torrent async create`() =
        runBlocking {
            val response = client.torrent.asyncCreate(magnet = TestConfig.magnetLink)
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `torrent create`() =
        runBlocking {
            val response = client.torrent.create(magnet = TestConfig.magnetLink)
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `torrent control`() =
        runBlocking {
            val response = client.torrent.control(torrentId = TestConfig.torrentId, operation = "delete")
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `torrent edit`() =
        runBlocking {
            val response = client.torrent.edit(torrentId = TestConfig.torrentId, tags = listOf("Test"))
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `torrent list`() =
        runBlocking {
            val response = client.torrent.list()
            assertTrue { response is ApiResult.Success && response.value.success }
            // println(response)
        }

    fun `torrent get`() =
        runBlocking {
            val response = client.torrent.get(id = TestConfig.torrentId)
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `torrent download link`() =
        runBlocking {
            // note: this may print the API token
            val response =
                client.torrent.downloadLink(
                    token = TestConfig.bearerToken,
                    torrentId = TestConfig.torrentId,
                )
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `torrent list availability`() =
        runBlocking {
            val response = client.torrent.listAvailability(hashes = listOf(TestConfig.torrentHash), listFiles = true)
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `torrent list availability object`() =
        runBlocking {
            val response =
                client.torrent.listAvailabilityObject(hashes = listOf(TestConfig.torrentHash), listFiles = false)
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `torrent list availability batch object`() =
        runBlocking {
            val response = client.torrent.listAvailabilityBatchObject(hashes = listOf(TestConfig.torrentHash))
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `torrent list availability batch`() =
        runBlocking {
            val response = client.torrent.listAvailabilityBatch(hashes = TestConfig.torrentHash.split(","))
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `torrent export magnet`() =
        runBlocking {
            // note: this API is not working
            val response = client.torrent.export(torrentId = TestConfig.torrentId, type = "file")
            assertTrue { response is ApiResult.Success }
        }

    fun `torrent get info`() =
        runBlocking {
            val response = client.torrent.info(hash = TestConfig.magnetHash)
            assertTrue { response is ApiResult.Success }
        }

    fun `torrent get full info`() =
        runBlocking {
            // val response = client.torrent.fullInfo(hash = TestConfig.magnetHash)
            val response = client.torrent.fullInfo(magnet = TestConfig.magnetLink, timeout = 15)
            // val response = client.torrent.fullInfo(fileBytes = File(TestConfig.torrentFilePath).readBytes(), fileName = "test.torrent")
            assertTrue { response is ApiResult.Success }
        }

    fun `torrent magnet to file`() =
        runBlocking {
            val response = client.torrent.magnetToFile(magnet = TestConfig.magnetLink)
            assertTrue { response is ApiResult.Success }
        }

    /***********
     * GENERAL *
     **********/

    fun `up status`() =
        runBlocking {
            val response = client.general.upStatus()
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun stats() =
        runBlocking {
            val response = client.general.stats()
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `changelog rss feed`() =
        runBlocking {
            val response = client.general.changelogRSSFeed()
            assertTrue { response is ApiResult.Success }
        }

    fun `changelog rss json feed`() =
        runBlocking {
            val response = client.general.changelogJson()
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `speed test files`() =
        runBlocking {
            val response = client.general.speedtestFiles()
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    /*******
     * WEB *
     *******/

    fun `web create`() =
        runBlocking {
            val response = client.web.create(link = TestConfig.hosterLink)
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `web control`() =
        runBlocking {
            val response = client.web.control(webId = TestConfig.webDownloadId, operation = "delete")
            assertTrue { response is ApiResult.Success && response.value.success }
        }
    
    fun `web list`() =
        runBlocking {
            val response = client.web.list()
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `web get`() =
        runBlocking {
            val response = client.web.get(id = TestConfig.webDownloadId)
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `web download link`() =
        runBlocking {
            // note: this may print the API token
            val response = client.web.downloadLink(token = TestConfig.bearerToken, webId = TestConfig.webDownloadId)
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `web list availability`() =
        runBlocking {
            val response = client.web.listAvailability(hashes = listOf(TestConfig.webHash))
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `web list availability object`() =
        runBlocking {
            val response = client.web.listAvailabilityObject(hashes = listOf(TestConfig.webHash))
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `web list availability batch object`() =
        runBlocking {
            val response = client.web.listAvailabilityBatchObject(hashes = listOf(TestConfig.webHash))
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `web list availability batch`() =
        runBlocking {
            val response = client.web.listAvailabilityBatch(hashes = listOf(TestConfig.webHash))
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `web hosters`() =
        runBlocking {
            val response = client.web.hosters()
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `web edit`() =
        runBlocking {
            val response = client.web.edit(webId = TestConfig.webDownloadId, tags = listOf("Test"))
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    /*****************
     * NOTIFICATIONS *
     ****************/

    fun `notifications rss feed`() =
        runBlocking {
            val response = client.notifications.rssFeed(TestConfig.bearerToken)
            assertTrue { response is ApiResult.Success }
        }

    fun `notifications json feed`() =
        runBlocking {
            val response = client.notifications.jsonFeed()
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `clear notifications`() =
        runBlocking {
            val response = client.notifications.clear(null)
            assertTrue { response is ApiResult.Success && response.value.success }
        }

    fun `send test notification`() =
        runBlocking {
            val response = client.notifications.sendTest()
            assertTrue { response is ApiResult.Success && response.value.success }
        }
}
