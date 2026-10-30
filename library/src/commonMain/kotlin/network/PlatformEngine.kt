package com.github.livingwithhippos.torbox.network

import io.ktor.client.engine.HttpClientEngineFactory

internal expect fun platformEngineFactory(): HttpClientEngineFactory<*>
