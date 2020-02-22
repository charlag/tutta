package com.charlag.tuta

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.Uint8Array
import kotlin.browser.window
import kotlin.reflect.KClass

actual fun platformName(): String {
    return "JS"
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
        val uint8Array = Uint8Array(this.asInt8Array().buffer)
        return window.btoa(js("String.fromCharCode.apply(null, new Uint8Array(uint8Array))") as String)
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

actual fun bytesToString(bytes: ByteArray): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun String.toBytes(): ByteArray {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun hexToPublicKey(hex: String): PublicKey {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}