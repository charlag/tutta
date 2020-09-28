package com.charlag.tuta.network

import io.ktor.client.engine.curl.Curl
import io.ktor.client.engine.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import kotlinx.serialization.json.Json

actual fun platformEngine(): HttpClientEngine = Curl.create { }
actual fun platformJsonSerializer(): JsonSerializer =
    KotlinxSerializer(Json { })