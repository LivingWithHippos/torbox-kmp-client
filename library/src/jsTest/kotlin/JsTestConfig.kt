object JsTestConfig {
    // In Node.js environment, access process.env directly
    private val env: dynamic
        get() =
            try {
                js("process.env")
            } catch (e: Throwable) {
                js("(typeof window !== 'undefined') ? window.__ENV__ : {}")
            }

    val bearerToken: String
        get() =
            env.TEST_BEARER_TOKEN?.toString()
                ?: error("TEST_BEARER_TOKEN env var not set")

    val torrentId: Int
        get() =
            env.TEST_TORRENT_ID?.toString()?.toIntOrNull()
                ?: error("TEST_TORRENT_ID env var not set or invalid")

    val magnetLink: String
        get() =
            env.TEST_MAGNET_LINK?.toString()
                ?: error("TEST_MAGNET_LINK env var not set")

    val webDownloadId: Int
        get() =
            env.TEST_WEB_ID?.toString()?.toIntOrNull()
                ?: error("TEST_WEB_ID env var not set or invalid")

    val hosterLink: String
        get() =
            env.HOSTER_LINK?.toString()
                ?: error("HOSTER_LINK env var not set")
}
