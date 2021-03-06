package com.charlag.tuta

import okio.ByteString.Companion.decodeBase64
import okio.ByteString.Companion.toByteString
import kotlin.reflect.KClass

actual fun platformName(): String {
    return "Android"
}

// We depend on Okio through OkHttp because of the websockets anyway, might as well
// use well-tested, fast and sane implementation. Can even use it in multiplatform later.
actual fun base64ToBytes(base64: String): ByteArray =
    base64.decodeBase64()!!.toByteArray()

actual fun ByteArray.toBase64(): String =
    this.toByteString(0, this.size).base64()

actual val KClass<*>.noReflectionName: String
    get() = this.java.simpleName