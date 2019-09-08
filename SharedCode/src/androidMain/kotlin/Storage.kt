package com.charlag.tuta

actual class Storage {
    actual suspend fun storeSecurely(key: String, value: String) {
    }

    actual suspend fun retrieveSecurely(key: String): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}