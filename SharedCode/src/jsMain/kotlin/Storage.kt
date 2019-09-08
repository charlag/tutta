package com.charlag.tuta

import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.browser.localStorage

actual class Storage {
    actual suspend fun storeSecurely(key: String, value: String) {
        // not very securely but will do for now
        localStorage[key] = value
    }

    actual suspend fun retrieveSecurely(key: String): String? =
        localStorage[key]
}