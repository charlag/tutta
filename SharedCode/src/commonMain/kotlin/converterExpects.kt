// Should not be a separate file, workaround for a compiler bug
// see https://youtrack.jetbrains.com/issue/KT-21186

package com.charlag.tuta

expect fun base64ToBytes(base64: String): ByteArray
expect fun ByteArray.toBase64(): String

expect fun bytesToString(bytes: ByteArray): String
expect fun String.toBytes(): ByteArray

expect fun hexToPublicKey(hex: String): PublicKey