package com.charlag.tuta

import com.charlag.tuta.entities.sys.typeInfos
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.util.*
import kotlin.reflect.KClass

actual fun platformName(): String {
    return "Android"
}

actual fun platformEngine(): HttpClientEngine {
    return OkHttp.create {  }
}

actual fun base64ToBytes(base64: String): ByteArray =
    Base64.getDecoder().decode(base64)

actual fun ByteArray.toBase64(): String =
    Base64.getEncoder().encodeToString(this)

@UnstableDefault
actual fun platformJsonSerializer(): JsonSerializer {
    return KotlinxSerializer(Json(JsonConfiguration.Default)).apply {
        typeInfos.forEach { (klass, _, _, serializer) ->
            @Suppress("UNCHECKED_CAST")
            setMapper(klass as KClass<Any>, serializer as KSerializer<Any>)
        }
    }
}

actual val KClass<*>.noReflectionName: String
    get() = this.java.simpleName

actual fun bytesToString(bytes: ByteArray): String = String(bytes)
actual fun String.toBytes(): ByteArray = this.toByteArray()