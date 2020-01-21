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
    suspend fun decryptRsaKey(value: ByteArray, key: ByteArray): PrivateKey
    suspend fun rsaDecrypt(value: ByteArray, key: PrivateKey): ByteArray
    fun generateRandomData(byteSize: Int): ByteArray
    suspend fun hash(bytes: ByteArray): ByteArray
    fun bcrypt(rounds: Int, passphrase: ByteArray, salt: ByteArray): ByteArray
}


data class PublicKey(
    val version: Int,
    val modulus: ByteArray
)

data class PrivateKey(
    val version: Int,
    val modulus: ByteArray,
    val privateExponent: ByteArray,
    val primeP: ByteArray,
    val primeQ: ByteArray,
    val primeExponentP: ByteArray,
    val primeExponentQ: ByteArray,
    val crtCoefficient: ByteArray
)

fun Cryptor.generateIV() = generateRandomData(16)

suspend fun Cryptor.generateKeyFromPassphrase(passphrase: String, salt: ByteArray): ByteArray {
    val passphraseBytes = hash(passphrase.toByteArray())

    return bcrypt(8, passphraseBytes, salt)
        .sliceArray(0 until 16)
}

val fixedIv: ByteArray = ByteArray(16) { 0x88.toByte() }

suspend fun Cryptor.encryptKey(plaintextKey: ByteArray, encryptionKey: ByteArray): ByteArray {
    return encrypt(plaintextKey, fixedIv, encryptionKey, false, false)
}

suspend fun Cryptor.decryptKey(encryptedKey: ByteArray, encryptionKey: ByteArray): ByteArray {
    return decrypt(fixedIv + encryptedKey, encryptionKey, false)
}

suspend fun Cryptor.aes128RandomKey(): ByteArray {
    return generateRandomData(16)
}