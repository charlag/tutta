package com.charlag.tuta

import at.favre.lib.crypto.bcrypt.BCrypt
import at.favre.lib.crypto.bcrypt.BCrypt.Version.VERSION_BC
import java.io.ByteArrayOutputStream
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

actual class Cryptor {
    actual suspend fun encrypt(
        value: ByteArray,
        iv: ByteArray,
        key: ByteArray,
        usePadding: Boolean,
        useMac: Boolean
    ): ByteArray {
        // TODO: encrypt without MAC too
        try {
            val cipher = getCipher(usePadding)
            val params = IvParameterSpec(iv)
            val subKeys = getSubKeys(bytesToKey(key))
            cipher.init(Cipher.ENCRYPT_MODE, subKeys.cKey, params)
            val tempOut = ByteArrayOutputStream()
            val data = iv + cipher.doFinal(value)
            tempOut.write(1) // marker that hmac is included
            val macBytes = hmac256(subKeys.mKey, data)
            tempOut.write(data)
            tempOut.write(macBytes)
            return tempOut.toByteArray()
        } catch (e: InvalidKeyException) {
            throw Error(e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException(e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw RuntimeException(e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

    private fun getCipher(usePadding: Boolean): Cipher {
        val mode = if (usePadding) AES_MODE_PADDING else AES_MODE_NO_PADDING
        return Cipher.getInstance(mode)
    }

    actual suspend fun decrypt(
        value: ByteArray,
        key: ByteArray,
        usePadding: Boolean
    ): ByteArray {
        try {
            var cKey = key
            val macIncluded = value.size % 2 == 1

            val actualEncryptedData = if (macIncluded) {
                val subKeys = getSubKeys(bytesToKey(key))
                cKey = subKeys.cKey.getEncoded()

                val cipherTextWithoutMac = value.copyOfRange(1, value.size - 32)

                val providedMacBytes = value.copyOfRange(value.size - 32, value.size)
                val computedMacBytes = hmac256(subKeys.mKey, cipherTextWithoutMac)

                if (!Arrays.equals(computedMacBytes, providedMacBytes)) {
                    throw Error("invalid mac")
                }
                cipherTextWithoutMac
            } else {
                value
            }
            val iv = actualEncryptedData.copyOfRange(0, AES_KEY_LENGTH_BYTES)
            val cipher = Cipher.getInstance(AES_MODE_PADDING)
            val params = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, bytesToKey(cKey), params)
            val result = cipher.doFinal(
                actualEncryptedData.copyOfRange(AES_KEY_LENGTH_BYTES, actualEncryptedData.size)
            )
            return result
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException(e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw RuntimeException(e)
        } catch (e: InvalidKeyException) {
            throw Error(e)
        }
    }

    actual fun generateRandomData(byteSize: Int): ByteArray {
        return ByteArray(byteSize).also { Random().nextBytes(it) }
    }

    /**
     * Converts the given byte array to a key.
     *
     * @param key The bytes representation of the key.
     * @return The key.
     */
    private fun bytesToKey(key: ByteArray): SecretKeySpec {
        if (key.size != AES_KEY_LENGTH_BYTES) {
            throw RuntimeException("invalid key length: " + key.size)
        }
        return SecretKeySpec(key, "AES")
    }


    private data class SubKeys(
        val cKey: SecretKeySpec,
        val mKey: ByteArray
    )

    private fun getSubKeys(key: SecretKeySpec): SubKeys {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(key.encoded)
        return SubKeys(
            cKey = SecretKeySpec(
                Arrays.copyOfRange(hash, 0, 16),
                "AES"
            ),
            mKey = Arrays.copyOfRange(hash, 16, 32)
        )
    }

    private fun hmac256(key: ByteArray, data: ByteArray): ByteArray {
        val HMAC_256 = "HmacSHA256"
        val macKey = SecretKeySpec(key, HMAC_256)
        try {
            val hmac = Mac.getInstance(HMAC_256)
            hmac.init(macKey)
            return hmac.doFinal(data)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException(e)
        }

    }

    companion object {
        const val AES_MODE_PADDING = "AES/CBC/PKCS5Padding"
        const val AES_MODE_NO_PADDING = "AES/CBC/NoPadding"
        const val AES_KEY_LENGTH = 128
        const val AES_KEY_LENGTH_BYTES = AES_KEY_LENGTH / 8
    }

    actual suspend fun hash(bytes: ByteArray): ByteArray {
        return MessageDigest.getInstance("SHA-256").digest(bytes)
    }

    actual fun bcrypt(
        rounds: Int,
        passphrase: ByteArray,
        salt: ByteArray
    ): ByteArray {
        return BCrypt.with(VERSION_BC).hashRaw(rounds, salt, passphrase).rawHash
    }
}