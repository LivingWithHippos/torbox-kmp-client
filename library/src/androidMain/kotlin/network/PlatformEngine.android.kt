package com.github.livingwithhippos.torbox.kmp.client.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

actual fun platformEngineFactory(): HttpClientEngineFactory<*>  = OkHttp