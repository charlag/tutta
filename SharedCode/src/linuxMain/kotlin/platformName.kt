package com.charlag.tuta

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.curl.Curl
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlin.reflect.KClass

actual fun platformName(): String = "Linux"
actual fun platformEngine(): HttpClientEngine = Curl.create { }
actual fun platformJsonSerializer(): JsonSerializer =
    KotlinxSerializer(Json(JsonConfiguration.Default))

actual val KClass<*>.noReflectionName: String
    get() = simpleName ?: "[Anonymous]"