package com.github.livingwithhippos.torbox.network

import io.ktor.client.engine.HttpClientEngineFactory

expect fun platformEngineFactory(): HttpClientEngineFactory<*>
