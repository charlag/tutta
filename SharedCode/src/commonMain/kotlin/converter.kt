package com.charlag.tuta


expect fun base64ToBytes(base64: String): ByteArray
expect fun ByteArray.toBase64(): String

expect fun bytesToString(bytes: ByteArray): String
expect fun String.toBytes(): ByteArray

fun base64ToBase64Url(base64: String): String =
    base64.replace('+', '-').replace('/', '_').replace("=", "")

fun base64UrlToBase64(base64url: String): String {
    val replaced = base64url.replace('-', '+').replace('_', '/')
    val padding = when (replaced.length % 4) {
        0 -> ""
        2 -> "=="
        3 -> "==="
        else -> error("Illegal Base64 string length: $base64url ${base64url.length}")
    }
    return replaced + padding
}