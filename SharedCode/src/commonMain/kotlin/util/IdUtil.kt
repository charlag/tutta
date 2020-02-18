package com.charlag.tuta.util

import com.charlag.tuta.base64ToBase64Ext
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.toBase64

internal fun timestampToHexGeneratedId(timestamp: Long, serverId: Int): String {
    // It's not pretty (or efficient) but it works and it's close to JS version

    // shifted 2 bits left, so the value covers 44 bits overall (42 timestamp + 2 shifted)
    val id = timestamp shl 2
    // add one zero for the missing 4 bits plus 4 more (2 bytes) plus 2 more for the server id to
    // get 9 bytes
    val unpadded = id.toString(16) + "00000" + serverId.toString().padStart(2, '0')
    return unpadded.padStart(18, '0')
}

fun timestmpToGeneratedId(timestamp: Long, serverId: Int): GeneratedId {
    val hex = timestampToHexGeneratedId(timestamp, serverId)
    return base64ToBase64Ext(hexToBase64(hex)).let(::GeneratedId)
}

// Might replace with Okio later
fun hexToBase64(hex: String): String = hexToBytes(hex).toBase64()

// Might replace with Okio later
fun hexToBytes(s: String): ByteArray {
    val len = s.length
    val data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
        val digit1 = charToHexDigit(s[i])
        val digit2 = charToHexDigit(s[i + 1])
        data[i / 2] = (digit1 * 16 + digit2).toByte()
        i += 2
    }
    return data
}

// From ktor
private fun charToHexDigit(c2: Char) = when (c2) {
    in '0'..'9' -> c2 - '0'
    in 'A'..'F' -> c2 - 'A' + 10
    in 'a'..'f' -> c2 - 'a' + 10
    else -> -1
}

// From ktor
private fun hexDigitToChar(digit: Int): Char = when (digit) {
    in 0..9 -> '0' + digit
    else -> 'A' + digit - 10
}