package com.charlag.tuta

import com.charlag.tuta.entities.TypeInfo
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import kotlin.collections.set
import kotlin.reflect.KClass

expect fun platformName(): String

fun createApplicationScreenMessage(): String {
    return "Kotlin Rocks on ${platformName()}"
}

expect fun platformEngine(): HttpClientEngine

expect fun platformJsonSerializer(): JsonSerializer

expect val KClass<*>.noReflectionName: String

// Maybe it should not be in common?
inline fun makeHttpClient(
    crossinline additionalConfig: HttpClientConfig<*>.() -> Unit = {}
): HttpClient = HttpClient(platformEngine()) {
    this.install(JsonFeature) {
        serializer = platformJsonSerializer()
    }
    this.install(Logging) {
        logger = Logger.DEFAULT
    }
    additionalConfig()
}

val typemodelMap: Map<String, TypeInfo<*>> by lazy {
    val typeModelMap = mutableMapOf<String, TypeInfo<*>>()
    // TODO: improve lookup so that names cannot collide
    for (typeInfo in com.charlag.tuta.entities.sys.typeInfos) {
        typeModelMap[typeInfo.typemodel.name] = typeInfo
    }
    for (typeInfo in com.charlag.tuta.entities.tutanota.typeInfos) {
        typeModelMap[typeInfo.typemodel.name] = typeInfo
    }
    typeModelMap
}