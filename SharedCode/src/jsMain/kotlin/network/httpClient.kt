package com.charlag.tuta.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration


actual fun platformEngine(): HttpClientEngine {
    return Js.create()
}

@UnstableDefault
actual fun platformJsonSerializer(): JsonSerializer {
    return KotlinxSerializer(Json(JsonConfiguration(strictMode = false)))
}