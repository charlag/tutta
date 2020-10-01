package com.charlag.tuta

import com.charlag.tuta.openssl.AES
import com.charlag.tuta.openssl.AES.AES_KEY_LENGTH_BYTES
import kotlinx.cinterop.*
import org.openssl.*

@OptIn(ExperimentalUnsignedTypes::class)
actual class Cryptor {
    /**
     * Symmetric encryption using com.charlag.tuta.openssl.AES. IV must be prepended.
     */
    actual suspend fun aesEncrypt(
        value: ByteArray,
        iv: ByteArray,
        key: ByteArray,
        usePadding: Boolean, // should default to true but it doesn't work
        useMac: Boolean // this should default to true too
    ): ByteArray {
        return if (useMac) {
            val subKeys = getSubKeys(key)
            val data = iv + AES.encrypt(value, subKeys.cKey, iv, usePadding)
            // marker that hmac is included
            byteArrayOf(1) + data + hmac256(subKeys.mKey, data)
        } else {
            iv + AES.encrypt(value, key, iv, usePadding)
        }
    }

    /**
     * Symmetric decryption using com.charlag.tuta.openssl.AES.
     */
    actual suspend fun aesDecrypt(
        value: ByteArray,
        key: ByteArray,
        usePadding: Boolean
    ): DecryptResult {
        if (key.size != AES_KEY_LENGTH_BYTES) {
            throw CryptoException("Key size invalid: ${key.size}")
        }
        var cKey = key
        val macIncluded = value.size % 2 == 1

        val encDataWithoutMacWithIv = if (macIncluded) {
            val subKeys = getSubKeys(key)
            cKey = subKeys.cKey

            val cipherTextWithoutMac = value.copyOfRange(1, value.size - 32)

            val providedMacBytes = value.copyOfRange(value.size - 32, value.size)
            val computedMacBytes = hmac256(subKeys.mKey, cipherTextWithoutMac)

            if (!computedMacBytes.contentEquals(providedMacBytes)) {
                throw CryptoException("Invaild mac")
            }
            cipherTextWithoutMac
        } else {
            value
        }
        // IV is prepended to the data, it has the same length as key
        val iv = encDataWithoutMacWithIv.copyOfRange(0, AES_KEY_LENGTH_BYTES)
        val actualEncData =
            encDataWithoutMacWithIv.copyOfRange(AES_KEY_LENGTH_BYTES, encDataWithoutMacWithIv.size)
        val result = AES.decrypt(actualEncData, cKey, iv, usePadding)
        return DecryptResult(data = result, iv = iv)
    }

    /**
     * Decrypt RSA with symmetric encryption
     */
    actual suspend fun decryptRsaKey(value: ByteArray, key: ByteArray): PrivateKey {
        TODO()
    }

    /**
     * Asymmetric encryption using RSA public key
     */
    actual suspend fun rsaEncrypt(value: ByteArray, publicKey: PublicKey): ByteArray {
        TODO()
    }

    /**
     * Asymmetric decryption using RSA private key
     */
    actual suspend fun rsaDecrypt(value: ByteArray, privateKey: PrivateKey): ByteArray {
        TODO()
    }

    actual fun generateRandomData(byteSize: Int): ByteArray {
        val bytes = ByteArray(byteSize)
        RAND_bytes(bytes.asUByteArray().refTo(0), byteSize)
        return bytes
    }

    actual suspend fun sha256hash(bytes: ByteArray): ByteArray {
        val ctx = EVP_MD_CTX_new()
        try {
            EVP_DigestInit(ctx, EVP_sha256())
            EVP_DigestUpdate(ctx, bytes.toCValues(), bytes.size.toULong())
            memScoped {
                val mdLength = alloc<UIntVar>()
                val mdValue = UByteArray(EVP_MAX_MD_SIZE)
                EVP_DigestFinal_ex(ctx, mdValue.refTo(0), mdLength.ptr)
                return mdValue.copyOfRange(0, mdLength.value.toInt()).toByteArray()
            }
        } finally {
            EVP_MD_CTX_free(ctx)
        }
    }

    /**
     * Raw BCrypt hash without null terminator, 24 byte out.
     */
    actual fun bcrypt(rounds: Int, passphrase: ByteArray, salt: ByteArray): ByteArray {
        val actualRounds = 1L shl rounds
        return BCryptOpenBSDProtocol.cryptRaw(actualRounds, salt, passphrase)
    }

    private data class SubKeys(
        val cKey: ByteArray,
        val mKey: ByteArray
    )

    private fun getSubKeys(key: ByteArray): SubKeys {
        val hash = sha256Digest(key)
        return SubKeys(
            cKey = hash.copyOfRange(0, 16),
            mKey = hash.copyOfRange(16, 32),
        )
    }

    private fun sha256Digest(value: ByteArray): ByteArray {
        val output = ByteArray(256)
        EVP_Digest(
            data = value.refTo(0),
            count = value.size.toULong(),
            md = output.asUByteArray().refTo(0),
            size = null,
            type = EVP_sha256(),
            impl = null
        )
        return output
    }

    private fun hmac256(key: ByteArray, data: ByteArray): ByteArray {
        val ctx = HMAC_CTX_new()
        try {
            val output = ByteArray(256)
            HMAC(
                evp_md = EVP_sha256(),
                key = key.refTo(0),
                key_len = key.size,
                d = data.asUByteArray().refTo(0),
                n = data.size.toULong(),
                md = output.asUByteArray().refTo(0),
                md_len = null
            )
            return output
        } finally {
            HMAC_CTX_free(ctx)
        }
    }
}