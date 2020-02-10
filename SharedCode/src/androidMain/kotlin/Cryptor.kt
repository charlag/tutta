package com.charlag.tuta

import at.favre.lib.crypto.bcrypt.BCrypt
import at.favre.lib.crypto.bcrypt.BCrypt.Version.VERSION_BC
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.security.*
import java.security.spec.MGF1ParameterSpec
import java.security.spec.RSAPrivateKeySpec
import java.security.spec.RSAPublicKeySpec
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import javax.crypto.spec.SecretKeySpec


actual class Cryptor {
    actual suspend fun encrypt(
        value: ByteArray,
        iv: ByteArray,
        key: ByteArray,
        usePadding: Boolean,
        useMac: Boolean
    ): ByteArray {
        try {
            val cipher = getCipher(usePadding)
            val params = IvParameterSpec(iv)

            val javaKey = bytesToKey(key)
            return if (useMac) {
                val subKeys = getSubKeys(javaKey)
                cipher.init(Cipher.ENCRYPT_MODE, subKeys.cKey, params)
                val data = iv + cipher.doFinal(value)
                val tempOut = ByteArrayOutputStream()
                tempOut.write(1) // marker that hmac is included
                val macBytes = hmac256(subKeys.mKey, data)
                tempOut.write(data)
                tempOut.write(macBytes)
                tempOut.toByteArray()
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, javaKey, params)
                return iv + cipher.doFinal(value)
            }
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
    ): DecryptResult {
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
                    throw CryptoException("Invaild mac")
                }
                cipherTextWithoutMac
            } else {
                value
            }
            val iv = actualEncryptedData.copyOfRange(0, AES_KEY_LENGTH_BYTES)
            val cipher =
                Cipher.getInstance(if (usePadding) AES_MODE_PADDING else AES_MODE_NO_PADDING)
            val params = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, bytesToKey(cKey), params)
            val result = cipher.doFinal(
                actualEncryptedData.copyOfRange(AES_KEY_LENGTH_BYTES, actualEncryptedData.size)
            )
            return DecryptResult(result, iv)
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

    actual suspend fun decryptRsaKey(value: ByteArray, key: ByteArray): PrivateKey {
        return hexToPrivateKey(bytesToHex(decrypt(value, key, true).data))
    }

    actual suspend fun rsaEncrypt(value: ByteArray, publicKey: PublicKey): ByteArray {
        return try {
            val cipher = Cipher.getInstance(RSA_ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey.toJava(), OAEP_PARAMETER_SPEC)
            cipher.doFinal(value)
        } catch (e: NoSuchAlgorithmException) {
            throw java.lang.RuntimeException(e)
        } catch (e: NoSuchPaddingException) {
            throw java.lang.RuntimeException(e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw java.lang.RuntimeException(e)
        } catch (e: BadPaddingException) {
            throw CryptoException("Error during RSA encryption", e)
        } catch (e: IllegalBlockSizeException) {
            throw CryptoException("Error during RSA encryption", e)
        } catch (e: InvalidKeyException) {
            throw CryptoException("Error during RSA encryption", e)
        }
    }

    actual suspend fun rsaDecrypt(value: ByteArray, privateKey: PrivateKey): ByteArray {
        return try {
            val cipher = Cipher.getInstance(RSA_ALGORITHM)
            cipher.init(
                Cipher.DECRYPT_MODE,
                privateKey.toJava(),
                OAEP_PARAMETER_SPEC
            )
            cipher.doFinal(value)
        } catch (e: BadPaddingException) {
            throw CryptoException("Error during RSA decrypt", e)
        } catch (e: InvalidKeyException) {
            throw CryptoException("Error during RSA decrypt", e)
        } catch (e: IllegalBlockSizeException) {
            throw CryptoException("Error during RSA decrypt", e)
        } catch (e: NoSuchAlgorithmException) { // These errors are not expected, fatal for the whole application and should be
            // reported.
            throw java.lang.RuntimeException("Error during RSA decrypt", e)
        } catch (e: NoSuchPaddingException) {
            throw java.lang.RuntimeException("Error during RSA decrypt", e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw java.lang.RuntimeException("Error during RSA decrypt", e)
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
        private const val RSA_ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
        private val OAEP_PARAMETER_SPEC = OAEPParameterSpec(
            "SHA-256",
            "MGF1",
            MGF1ParameterSpec.SHA256,
            PSource.PSpecified.DEFAULT
        )
        private val RSA_PUBLIC_EXPONENT = BigInteger.valueOf(65537)
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

    private fun PrivateKey.toJava(): java.security.PrivateKey {
        val modulus = BigInteger(this.modulus)
        val privateExponent = BigInteger(this.privateExponent)
        return KeyFactory.getInstance("RSA")
            .generatePrivate(RSAPrivateKeySpec(modulus, privateExponent))
    }

    private fun PublicKey.toJava(): java.security.PublicKey {
        return KeyFactory.getInstance("RSA")
            .generatePublic(RSAPublicKeySpec(BigInteger(modulus), RSA_PUBLIC_EXPONENT))
    }
}
