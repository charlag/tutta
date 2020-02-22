package com.charlag.tuta.util

import com.charlag.tuta.base64ToBase64Ext
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.hexToBase64

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