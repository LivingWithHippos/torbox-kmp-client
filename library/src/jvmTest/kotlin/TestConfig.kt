package com.github.livingwithhippos.torbox

import java.io.File
import java.io.FileInputStream
import java.util.Properties

object TestConfig {
    private val props: Properties by lazy {
        Properties().also { props ->
            val file = File("../local.properties")
            if (file.exists()) {
                props.load(FileInputStream(file))
            }
        }
    }

    val bearerToken: String get() =
        props.getProperty("test.bearerToken")
            ?: error("test.bearerToken missing from local.properties")

    val torrentId: Int get() =
        props.getProperty("test.torrentId")?.toInt()
            ?: error("test.torrentId missing from local.properties")

    val magnetLink: String get() =
        props.getProperty("test.magnetLink")
            ?: error("test.magnetLink missing from local.properties")

    val torrentFilePath: String get() =
        props.getProperty("test.torrentFilePath")
            ?: error("test.torrentFilePath missing from local.properties")

    /**
     * This may be the Torbox inner hash, not the magnet one, use [magnetHash] for that
     */
    val torrentHash: String get() =
        props.getProperty("test.torrentHash")
            ?: error("test.torrentHash missing from local.properties")

    /**
     * the standard magnet/torrent hash
     */
    val magnetHash: String get() =
        props.getProperty("test.torremagnetHashntHash")
            ?: error("test.magnetHash missing from local.properties")

    val webDownloadId: Int get() =
        props.getProperty("test.webDownloadId")?.toInt()
            ?: error("test.webDownloadId missing from local.properties")

    val webHash: String get() =
        props.getProperty("test.webHash")
            ?: error("test.webHash missing from local.properties")

    val hosterLink: String get() =
        props.getProperty("test.hosterLink")
            ?: error("test.hosterLink missing from local.properties")
}
