package com.github.livingwithhippos.torbox.kmp.client

import com.github.livingwithhippos.torbox.kmp.client.client.TorboxClient
import com.github.livingwithhippos.torbox.kmp.client.model.ExportTorrentResultDto
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult
import com.github.livingwithhippos.torbox.kmp.client.network.RestClientConfig
import kotlinx.coroutines.test.runTest
import kotlin.io.path.Path
import kotlin.io.path.readBytes
import kotlin.test.Test
import kotlin.test.assertTrue
import co.touchlab.kermit.Logger as KermitLogger

class JvmApiTest {
    private val client = TorboxClient.create(RestClientConfig(bearerToken = TestConfig.bearerToken))

    @Test
    fun getTorrents() =
        runTest {
            val listResult = client.torrents.getTorrents()
            KermitLogger.i(listResult.toString())
            assertTrue { listResult is ApiResult.Success }

            val singleResult = client.torrents.getTorrents(id = TestConfig.torrentId)
            KermitLogger.i(singleResult.toString())
            assertTrue { singleResult is ApiResult.Success }
        }

    @Test
    fun `create torrent from magnet`() =
        runTest {
            val result =
                client.torrents.createTorrent(
                    magnet = TestConfig.magnetLink,
                    allowZip = false,
                    addOnlyIfCached = false,
                )

            KermitLogger.i(result.toString())

            assertTrue { result is ApiResult.Success && result.value.success }
        }

    @Test
    fun `create torrent from file`() =
        runTest {
            val torrentBytes = Path(TestConfig.torrentFilePath).readBytes()

            val result =
                client.torrents.createTorrent(
                    fileBytes = torrentBytes,
                )

            KermitLogger.i(result.toString())

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

            KermitLogger.i(result.toString())

            assertTrue { result is ApiResult.Success && result.value.success }
        }

    @Test
    fun `torrent cache`() =
        runTest {
            val resultList =
                client.torrents.getTorrentAvailabilityList(
                    hashes = listOf(TestConfig.torrentHash),
                )

            KermitLogger.i(resultList.toString())

            assertTrue { resultList is ApiResult.Success }

            val resultObject =
                client.torrents.getTorrentAvailabilityObject(
                    hashes = listOf(TestConfig.torrentHash),
                )

            KermitLogger.i(resultObject.toString())

            assertTrue { resultObject is ApiResult.Success && resultObject.value.success }
        }

    @Test
    fun `torrent batch cache`() =
        runTest {
            val resultList =
                client.torrents.getTorrentAvailabilityBatchObject(
                    hashes = listOf(TestConfig.torrentHash),
                )

            KermitLogger.i(resultList.toString())

            assertTrue { resultList is ApiResult.Success }
        }

    @Test
    fun `control torrent`() =
        runTest {
            val result =
                client.torrents.controlTorrent(
                    torrentId = TestConfig.torrentId,
                    operation = "Reannounce",
                )

            KermitLogger.i(result.toString())

            assertTrue { result is ApiResult.Success }
        }

    @Test
    fun `edit torrent`() =
        runTest {
            val result =
                client.torrents.editTorrent(
                    torrentId = TestConfig.torrentId,
                    name = "New Torrent Name",
                )

            KermitLogger.i(result.toString())

            assertTrue { result is ApiResult.Success }
        }

    @Test
    fun `export torrent magnet`() =
        runTest {
            // todo: chek these tests, this returns null and the other 4 bytes
            val result =
                client.torrents.exportTorrentData(
                    torrentId = TestConfig.torrentId,
                    type = "magnet",
                )
            KermitLogger.i(result.toString())
            assertTrue { result is ApiResult.Success && result.value is ExportTorrentResultDto.Magnet }
        }

    @Test
    fun `export torrent file`() =
        runTest {
            val result =
                client.torrents.exportTorrentData(
                    torrentId = TestConfig.torrentId,
                    type = "file",
                )
            KermitLogger.i(result.toString())
            assertTrue { result is ApiResult.Success && result.value is ExportTorrentResultDto.File }
        }

    @Test
    fun `magnet to file`() =
        runTest {
            val result = client.torrents.magnetToFile(TestConfig.magnetLink)

            KermitLogger.i(result.toString())
            assertTrue { result is ApiResult.Success && result.value.isNotEmpty() }
        }

    @Test
    fun `unrestrict link`() =
        runTest {
            val result =
                client.web.createWebLink(
                    link = TestConfig.hosterLink,
                )

            KermitLogger.i(result.toString())

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

            KermitLogger.i(result.toString())

            assertTrue { result is ApiResult.Success && result.value.success }
        }

    @Test
    fun getWebLinks() =
        runTest {
            val listResult = client.web.getWebLinks()
            KermitLogger.i(listResult.toString())
            assertTrue { listResult is ApiResult.Success && listResult.value.success }

            val singleResult = client.web.getWebLinks(id = TestConfig.webDownloadId)
            KermitLogger.i(singleResult.toString())
            assertTrue { singleResult is ApiResult.Success && singleResult.value.success }
        }

    @Test
    fun `get hoster list`() =
        runTest {
            val listResult = client.web.getHosters()
            KermitLogger.i(listResult.toString())
            assertTrue { listResult is ApiResult.Success && listResult.value.success }
        }

    @Test
    fun `get usenet list`() =
        runTest {
            val listResult = client.usenet.getUsenetList()
            KermitLogger.i(listResult.toString())
            assertTrue { listResult is ApiResult.Success && listResult.value.success }
        }

    @Test
    fun `get up status`() =
        runTest {
            val result = client.general.getUpStatus()
            KermitLogger.i(result.toString())
            assertTrue { result is ApiResult.Success && result.value.success }
        }

    @Test
    fun `get stats`() =
        runTest {
            val result = client.general.getStats()
            KermitLogger.i(result.toString())
            assertTrue { result is ApiResult.Success && result.value.success }
        }

    @Test
    fun `get changeLog`() =
        runTest {
            val result = client.general.getChangelog()
            KermitLogger.i(result.toString())
            assertTrue { result is ApiResult.Success }
        }

    @Test
    fun `get changeLog json`() =
        runTest {
            val result = client.general.getChangelogJson()
            KermitLogger.i(result.toString())
            assertTrue { result is ApiResult.Success }
        }

    @Test
    fun `get speed test`() =
        runTest {
            val result = client.general.getSpeedtestFiles()
            KermitLogger.i(result.toString())
            assertTrue { result is ApiResult.Success }
        }

    @Test
    fun `get notifications`() =
        runTest {
            val result = client.notifications.getNotifications()
            KermitLogger.i(result.toString())
            assertTrue { result is ApiResult.Success }
        }
}
