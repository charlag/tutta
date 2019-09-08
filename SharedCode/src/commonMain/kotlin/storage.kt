package com.charlag.tuta

expect class Storage {
    suspend fun storeSecurely(key: String, value: String)

    suspend fun retrieveSecurely(key: String): String?
}