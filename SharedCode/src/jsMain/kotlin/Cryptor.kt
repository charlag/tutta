package com.charlag.tuta

import Bcrypt
import kotlinx.coroutines.await
import org.khronos.webgl.*
import org.w3c.dom.Window
import kotlin.browser.window
import kotlin.js.Promise

val subtleCrypto = window.crypto.subtle

actual class Cryptor {
    @UseExperimental(ExperimentalUnsignedTypes::class)
    suspend actual fun aesEncrypt(
        value: ByteArray,
        iv: ByteArray,
        key: ByteArray,
        usePadding: Boolean,
        useMac: Boolean
    ): ByteArray {
        val cryptoKey =
            subtleCrypto.importKey("raw", key, AES_ALGORITHM, false, arrayOf("encrypt")).await()
        return subtleCrypto.encrypt(jsObject {
            name = AES_ALGORITHM
            this.iv = iv
        }, cryptoKey, value)
            .await()
            .toByteArray()
    }

    suspend actual fun aesDecrypt(
        value: ByteArray,
        key: ByteArray,
        usePadding: Boolean
    ): DecryptResult {
        val cryptoKey = subtleCrypto.importKey("raw", key, AES_ALGORITHM, false, arrayOf("decrypt"))
            .await()
        val iv = generateIV() // TODO
        return subtleCrypto.decrypt(jsObject {
            name = AES_ALGORITHM
            this.iv =  iv
        }, cryptoKey, value)
            .await()
            .toByteArray()
            .let {
                DecryptResult(iv, it)
            }
    }

    actual fun generateRandomData(byteSize: Int): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual suspend fun sha256hash(bytes: ByteArray): ByteArray {
        return subtleCrypto.digest("SHA-256", bytes.asInt8Array()).await().toByteArray()
    }

    actual fun bcrypt(rounds: Int, passphrase: ByteArray, salt: ByteArray): ByteArray {
        return Bcrypt().crypt_raw(
            byteArrayToSignedBytes(passphrase),
            byteArrayToSignedBytes(salt),
            8
        ).toByteArray()
    }


    private companion object {
        const val AES_ALGORITHM = "AES-CBC"
    }

    actual suspend fun decryptRsaKey(
        value: ByteArray,
        key: ByteArray
    ): PrivateKey {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual suspend fun rsaDecrypt(
        value: ByteArray,
        privateKey: PrivateKey
    ): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual suspend fun rsaEncrypt(
        value: ByteArray,
        publicKey: PublicKey
    ): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


private fun byteArrayToSignedBytes(array: ByteArray): Array<Byte> {
    return js("Array.from(new Uint8Array(new Int8Array(array)))") as Array<Byte>
}

typealias CryptoAlgorithm = Map<String, Any>

external class CryptoKey {
    val type: String
    val extractable: Boolean
    val algorithm: CryptoAlgorithm
    val usages: Array<String>
}

@Suppress("unused")
inline val Window.crypto: Crypto
    get() = js("window.crypto")

external interface Crypto {
    val subtle: SubtleCrypto
    fun getRandomValues(output: ArrayBufferView)
}

external interface SubtleCrypto {
    fun encrypt(algorithm: CryptoAlgorithm, key: CryptoKey, data: ByteArray): Promise<ArrayBuffer>
    fun decrypt(algorithm: CryptoAlgorithm, key: CryptoKey, data: ByteArray): Promise<ArrayBuffer>
    fun digest(algorithm: String, data: ArrayBufferView): Promise<ArrayBuffer>
    fun importKey(
        format: String,
        keyData: Any,
        algorithm: Any,
        extractable: Boolean,
        keyUsages: Array<String>
    ): Promise<CryptoKey>

//    fun generatekey()
//    fun derivekey()
//    fun exportKey()
//    ...
}