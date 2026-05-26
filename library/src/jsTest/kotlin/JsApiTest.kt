import com.github.livingwithhippos.torbox.kmp.client.client.TorboxClient
import com.github.livingwithhippos.torbox.kmp.client.network.ApiResult
import com.github.livingwithhippos.torbox.kmp.client.network.RestClientConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.promise
import kotlin.test.Test
import kotlin.test.assertTrue
import co.touchlab.kermit.Logger as KermitLogger

class JsApiTest {
    private val client = TorboxClient.create(RestClientConfig(bearerToken = JsTestConfig.bearerToken))
    private val scope = CoroutineScope(Dispatchers.Main)

    @Test
    fun getTorrents() =
        scope.promise {
            val listResult = client.api.getTorrents()
            KermitLogger.i(listResult.toString())
            assertTrue { listResult is ApiResult.Success }

            val singleResult = client.api.getTorrents(id = JsTestConfig.torrentId)
            KermitLogger.i(singleResult.toString())
            assertTrue { singleResult is ApiResult.Success }
        }
}
