package com.github.livingwithhippos.torbox.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.Js

actual fun platformEngineFactory(): HttpClientEngineFactory<*> = Js
