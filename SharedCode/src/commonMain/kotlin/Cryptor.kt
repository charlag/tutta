package com.charlag.tuta

import io.ktor.utils.io.core.toByteArray

/**
 * Common interface for all cryptographic operations.
 */
expect class Cryptor() {
    /**
     * Symmetric encryption using AES. IV must be prepended.
     */
    suspend fun aesEncrypt(
        value: ByteArray,
        iv: ByteArray,
        key: ByteArray,
        usePadding: Boolean, // should default to true but it doesn't work
        useMac: Boolean // this should default to true too
    ): ByteArray

    /**
     * Symmetric decryption using AES.
     */
    suspend fun aesDecrypt(value: ByteArray, key: ByteArray, usePadding: Boolean): DecryptResult

    /**
     * Decrypt RSA with symmetric encryption
     */
    suspend fun decryptRsaKey(value: ByteArray, key: ByteArray): PrivateKey

    /**
     * Asymmetric encryption using RSA public key
     */
    suspend fun rsaEncrypt(value: ByteArray, publicKey: PublicKey): ByteArray

    /**
     * Asymmetric decryption using RSA private key
     */
    suspend fun rsaDecrypt(value: ByteArray, privateKey: PrivateKey): ByteArray

    fun generateRandomData(byteSize: Int): ByteArray
    suspend fun sha256hash(bytes: ByteArray): ByteArray
    /**
     * Raw BCrypt hash without null terminator, 24 byte out.
     */
    fun bcrypt(rounds: Int, passphrase: ByteArray, salt: ByteArray): ByteArray
}

class DecryptResult(
    val data: ByteArray,
    val iv: ByteArray
)


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
private const val BCRYPT_ROUNDS = 8

suspend fun Cryptor.generateKeyFromPassphrase(passphrase: String, salt: ByteArray): ByteArray {
    val passphraseBytes = sha256hash(passphrase.toByteArray())

    return bcrypt(BCRYPT_ROUNDS, passphraseBytes, salt)
        .sliceArray(0 until 16)
}

val fixedIv: ByteArray = ByteArray(16) { 0x88.toByte() }

suspend fun Cryptor.encryptKey(plaintextKey: ByteArray, encryptionKey: ByteArray): ByteArray {
    return aesEncrypt(plaintextKey, fixedIv, encryptionKey, false, false).run {
        copyOfRange(fixedIv.size, this.size)
    }
}

suspend fun Cryptor.decryptKey(encryptedKey: ByteArray, encryptionKey: ByteArray): ByteArray {
    return aesDecrypt(fixedIv + encryptedKey, encryptionKey, false).data
}

suspend fun Cryptor.aes128RandomKey(): ByteArray {
    return generateRandomData(16)
}

suspend fun Cryptor.encryptString(data: String, encryptionKey: ByteArray): ByteArray {
    return aesEncrypt(data.toBytes(), generateIV(), encryptionKey, true, true)
}

class CryptoException(message: String, cause: Exception? = null) : Exception(message, cause)