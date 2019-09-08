package com.charlag.tuta

import kotlinx.io.core.toByteArray

expect class Cryptor() {
    suspend fun encrypt(
        value: ByteArray,
        iv: ByteArray,
        key: ByteArray,
        usePadding: Boolean = true,
        useMac: Boolean = true
    ): ByteArray

    suspend fun decrypt(value: ByteArray, key: ByteArray, usePadding: Boolean = true): ByteArray
    fun generateRandomData(byteSize: Int): ByteArray
    suspend fun hash(bytes: ByteArray): ByteArray
    fun bcrypt(rounds: Int, passphrase: ByteArray, salt: ByteArray): ByteArray
}

fun Cryptor.generateIV() = generateRandomData(16)

suspend fun Cryptor.generateKeyFromPassphrase(passphrase: String, salt: ByteArray): ByteArray {
    val passphraseBytes = hash(passphrase.toByteArray())

    return bcrypt(8, passphraseBytes, salt)
        .sliceArray(0 until 16)
}

val fixedIv: ByteArray = ByteArray(16) { 0x88.toByte() }

suspend fun Cryptor.encryptKey(pplaintextKey: ByteArray, encryptionKey: ByteArray): ByteArray {
    return encrypt(pplaintextKey, encryptionKey, encryptionKey, false, false)
}

suspend fun Cryptor.decryptKey(encryptedKey: ByteArray, encryptionKey: ByteArray): ByteArray {
    return decrypt(encryptedKey, fixedIv + encryptionKey, false)
}