package com.github.livingwithhippos.torbox.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO

actual fun platformEngineFactory(): HttpClientEngineFactory<*> = CIO
