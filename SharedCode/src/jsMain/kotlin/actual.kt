package com.charlag.tuta

import Bcrypt
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.io.core.toByteArray
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import kotlin.browser.window
import kotlin.reflect.KClass

actual fun platformName(): String {
    return "JS"
}

actual fun platformEngine(): HttpClientEngine {
    return Js.create()
}

@UnstableDefault
actual fun platformJsonSerializer(): JsonSerializer {
    return KotlinxSerializer(Json(JsonConfiguration(strictMode = false)))
}

private val global: dynamic = js("(typeof global !== 'undefined') ? global : self")

actual fun base64ToBytes(base64: String): ByteArray {
    if (base64.length % 4 != 0) throw Error("Oopsie woopsie wrong base64 length ${base64.length}")
    val binary = global.atob(base64) as String
    return ByteArray(binary.length) { pos -> binary[pos].toByte() }
}

@Suppress("CAST_NEVER_SUCCEEDS")
inline fun Int8Array.asByteArray(): ByteArray = this as ByteArray


@Suppress("NOTHING_TO_INLINE", "CAST_NEVER_SUCCEEDS")
inline fun ByteArray.asInt8Array(): Int8Array = this as Int8Array

@Suppress("CAST_NEVER_SUCCEEDS")
inline fun ArrayBuffer.toByteArray(): ByteArray = Int8Array(this) as ByteArray

actual fun ByteArray.toBase64(): String {
    if (this.size < 512) {
        // Apply fails on big arrays fairly often. We tried it with 60000 but if you're already
        // deep in the stack than we cannot allocate such a big argument array.
        return window.btoa(js("String.fromCharCode.apply(null, new Uint8Array(bytes))") as String)
    }
    var binary = ""
    this.forEach {
        binary += js("String.fromCharcode(bytes[i])") as String
    }
    return window.btoa(binary)
}

inline fun jsObject(init: dynamic.() -> Unit): dynamic {
    val o = js("{}")
    init(o)
    return o
}

actual val KClass<*>.noReflectionName: String
    get() = this.simpleName!!