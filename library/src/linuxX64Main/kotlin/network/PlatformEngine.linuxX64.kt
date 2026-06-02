package com.github.livingwithhippos.torbox.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.curl.Curl

actual fun platformEngineFactory(): HttpClientEngineFactory<*> = Curl
