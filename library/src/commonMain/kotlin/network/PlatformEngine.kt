package com.github.livingwithhippos.torbox.kmp.client.network

import io.ktor.client.engine.HttpClientEngineFactory

expect fun platformEngineFactory(): HttpClientEngineFactory<*>
