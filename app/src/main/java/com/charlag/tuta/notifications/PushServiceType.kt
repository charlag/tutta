package com.charlag.tuta.notifications

enum class PushServiceType(val raw: Long) {
    ANDROID(0),
    IOS(1),
    EMAIL(2),
    SSE(3)
}
