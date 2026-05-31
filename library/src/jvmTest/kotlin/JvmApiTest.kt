package com.github.livingwithhippos.torbox.kmp.client

import com.github.livingwithhippos.torbox.kmp.client.client.TorboxClient
import com.github.livingwithhippos.torbox.kmp.client.model.ExportTorrentResultDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult
import com.github.livingwithhippos.torbox.kmp.client.network.RestClientConfig
import io.ktor.client.plugins.logging.LogLevel
import kotlinx.coroutines.test.runTest
import kotlin.io.path.Path
import kotlin.io.path.readBytes
import kotlin.test.Test
import kotlin.test.assertTrue

class JvmApiTest {
    private val client = TorboxClient.create(RestClientConfig(bearerToken = TestConfig.bearerToken))

    @Test
    fun getTorrents() =
        runTest {
            val listResult = client.torrents.get()
            assertTrue { listResult is ApiResult.Success }

            val singleResult = client.torrents.get(id = TestConfig.torrentId)
            assertTrue { singleResult is ApiResult.Success }
        }

    @Test
    fun `create torrent from magnet`() =
        runTest {
            val result =
                client.torrents.create(
                    magnet = TestConfig.magnetLink,
                    allowZip = false,
                    addOnlyIfCached = false,
                )

            assertTrue { result is ApiResult.Success && result.value.success }
        }

    @Test
    fun `create torrent from file`() =
        runTest {
            val torrentBytes = Path(TestConfig.torrentFilePath).readBytes()

            val result =
                client.torrents.create(
                    fileBytes = torrentBytes,
                )

            assertTrue { result is ApiResult.Success && result.value.success }
        }

    @Test
    fun `unrestrict torrent`() =
        runTest {
            // change this to simulate a success = false error
            val result =
                client.torrents.requestTorrentDownloadLink(
                    token = TestConfig.bearerToken,
                    torrentId = TestConfig.torrentId,
                )

            assertTrue { result is ApiResult.Success && result.value.success }
        }

    @Test
    fun `torrent cache`() =
        runTest {
            val resultList =
                client.torrents.getAvailabilityList(
                    hashes = listOf(TestConfig.torrentHash),
                )

            assertTrue { resultList is ApiResult.Success }

            val resultObject =
                client.torrents.getAvailabilityObject(
                    hashes = listOf(TestConfig.torrentHash),
                )

            assertTrue { resultObject is ApiResult.Success && resultObject.value.success }
        }

    @Test
    fun `torrent batch cache`() =
        runTest {
            val resultList =
                client.torrents.getAvailabilityBatchObject(
                    hashes = listOf(TestConfig.torrentHash),
                )

            assertTrue { resultList is ApiResult.Success }
        }

    @Test
    fun `control torrent`() =
        runTest {
            // this test must use a valid torrent id (check the list API)
            val result =
                client.torrents.controlTorrent(
                    torrentId = TestConfig.torrentId,
                    operation = "Reannounce",
                )

            assertTrue { result is ApiResult.Success }
        }

    @Test
    fun `edit torrent`() =
        runTest {
            val result =
                client.torrents.edit(
                    torrentId = TestConfig.torrentId,
                    name = "New Torrent Name",
                )

            assertTrue { result is ApiResult.Success }
        }

    fun `export torrent magnet`() =
        runTest {
            // fixme: chek these tests, this returns null and `export torrent file` returns 4 bytes
            val result =
                client.torrents.export(
                    torrentId = TestConfig.torrentId,
                    type = "magnet",
                )
            assertTrue { result is ApiResult.Success && result.value is ExportTorrentResultDto.Magnet }
        }

    @Test
    fun `export torrent file`() =
        runTest {
            val result =
                client.torrents.export(
                    torrentId = TestConfig.torrentId,
                    type = "file",
                )
            assertTrue { result is ApiResult.Success && result.value is ExportTorrentResultDto.File }
        }

    @Test
    fun `magnet to file`() =
        runTest {
            val result = client.torrents.magnetToFile(TestConfig.magnetLink)

            assertTrue { result is ApiResult.Success && result.value.isNotEmpty() }
        }

    @Test
    fun `unrestrict link`() =
        runTest {
            val result =
                client.web.createWebLink(
                    link = TestConfig.hosterLink,
                )

            assertTrue { result is ApiResult.Success && result.value.success }
        }

    @Test
    fun `download link`() =
        runTest {
            val result =
                client.web.requestDownloadLink(
                    token = TestConfig.bearerToken,
                    webId = TestConfig.webDownloadId,
                )

            assertTrue { result is ApiResult.Success && result.value.success }
        }

    @Test
    fun getWebLinks() =
        runTest {
            val listResult = client.web.getWebLinks()
            assertTrue { listResult is ApiResult.Success && listResult.value.success }

            val singleResult = client.web.getWebLinks(id = TestConfig.webDownloadId)
            assertTrue { singleResult is ApiResult.Success && singleResult.value.success }
        }

    @Test
    fun `get hoster list`() =
        runTest {
            val listResult = client.web.getHosters()
            assertTrue { listResult is ApiResult.Success && listResult.value.success }
        }

    @Test
    fun `get usenet list`() =
        runTest {
            val listResult = client.usenet.getUsenetList()
            assertTrue { listResult is ApiResult.Success && listResult.value.success }
        }

    @Test
    fun `get up status`() =
        runTest {
            val result = client.general.upStatus()
            assertTrue { result is ApiResult.Success && result.value.success }
        }

    @Test
    fun `get stats`() =
        runTest {
            val result = client.general.stats()
            assertTrue { result is ApiResult.Success && result.value.success }
        }

    @Test
    fun `get changeLog`() =
        runTest {
            val result = client.general.changelogRSSFeed()
            assertTrue { result is ApiResult.Success }
        }

    @Test
    fun `get changeLog json`() =
        runTest {
            val result = client.general.changelogJson()
            assertTrue { result is ApiResult.Success }
        }

    @Test
    fun `get speed test`() =
        runTest {
            val result = client.general.speedtestFiles()
            assertTrue { result is ApiResult.Success }
        }

    @Test
    fun `get notifications`() =
        runTest {
            val result = client.notifications.jsonFeed()
            assertTrue { result is ApiResult.Success }
        }

    @Test
    fun `get user data`() =
        runTest {
            val result = client.user.getUserData()
            assertTrue { result is ApiResult.Success }
        }

    @Test
    fun `get referral`() =
        runTest {
            val result = client.user.getReferral()
            assertTrue { result is ApiResult.Success }
        }

    @Test
    fun `get subscriptions`() =
        runTest {
            // this returns success but no data
            val result = client.user.getSubscriptions()
            assertTrue { result is ApiResult.Success }
        }

    @Test
    fun `get user search engines`() =
        runTest {
            val result = client.user.getSearchEngines()
            assertTrue { result is ApiResult.Success }
        }

    fun `get user stats`() =
        runTest {
            // fixme: this returns 500
            val result = client.user.getUserStats()
            assertTrue { result is ApiResult.Success }
        }

    @Test
    fun `get user rss`() =
        runTest {
            val result = client.rss.userFeeds()
            assertTrue { result is ApiResult.Success }
        }

    @Test
    fun `get all integrations`() =
        runTest {
            val result = client.integrations.jobs()
            assertTrue { result is ApiResult.Success }
        }
}
