package com.github.livingwithhippos.torbox.kmp.client.network

import io.ktor.client.engine.darwin.Darwin


actual fun platformEngineFactory(): io.ktor.client.engine.HttpClientEngineFactory<*> = Darwin