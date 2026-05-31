import com.github.livingwithhippos.torbox.kmp.client.client.TorboxClient
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult
import com.github.livingwithhippos.torbox.kmp.client.network.RestClientConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.promise
import kotlin.test.Test
import kotlin.test.assertTrue

class JsApiTest {
    private val client = TorboxClient.create(RestClientConfig(bearerToken = JsTestConfig.bearerToken))
    private val scope = CoroutineScope(Dispatchers.Main)

    @Test
    fun `get Torrents`() =
        scope.promise {
            val listResult = client.torrents.getTorrent()
            assertTrue { listResult is ApiResult.Success }

            val singleResult = client.torrents.getTorrent(id = JsTestConfig.torrentId)
            assertTrue { singleResult is ApiResult.Success }
        }

    @Test
    fun `get user data`() =
        scope.promise {
            val result = client.user.getUserData()
            assertTrue { result is ApiResult.Success }
        }
}
