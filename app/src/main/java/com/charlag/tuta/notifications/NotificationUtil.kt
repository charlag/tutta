@file:JvmName("NotificationUtil")

package com.charlag.tuta.notifications

import android.os.Build
import com.charlag.tuta.*
import io.ktor.utils.io.core.String
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.Key
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec


// File mostly needed as a bridge for Java-based notifications code which wasn't converted yet

internal fun bytesToBase64(byteArray: ByteArray) = byteArray.toBase64()

internal fun base64ToBytes(base64: String) = com.charlag.tuta.base64ToBytes(base64)

@Throws(CryptoException::class)
internal fun decrypt(cryptor: Cryptor, value: ByteArray, key: ByteArray) =
    runBlocking {
        cryptor.aesDecrypt(value, key, true)
    }

@Throws(CryptoException::class)
fun decryptDate(
    encryptedData: String,
    cryptor: Cryptor,
    sessionKey: ByteArray
): Date? {
    val decString = decryptString(encryptedData, cryptor, sessionKey)
    return Date(decString.toLong())
}

@Throws(CryptoException::class)
fun decryptString(
    encryptedData: String,
    cryptor: Cryptor,
    sessionKey: ByteArray?
): String {
    val decBytes =
        decrypt(cryptor, encryptedData.toByteArray(), sessionKey!!).data
    return String(decBytes, StandardCharsets.UTF_8)
}

@Throws(CryptoException::class)
fun decryptNumber(
    encryptedData: String,
    crypto: Cryptor,
    sessionKey: ByteArray?
): Long {
    val stringValue = decryptString(encryptedData, crypto, sessionKey)
    return stringValue.toLong()
}

@Throws(CryptoException::class)
fun decryptKey(
    cryptor: Cryptor,
    encryptedKey: ByteArray,
    encryptionKey: ByteArray
) = runBlocking {
    cryptor.decryptKey(encryptedKey, encryptionKey)
}

@Throws(CryptoException::class)
fun encryptKey(
    cryptor: Cryptor,
    plaintextKey: ByteArray,
    encryptionKey: ByteArray
) = runBlocking {
    cryptor.encryptKey(plaintextKey, encryptionKey)
}

// We need these methods because Android Keystore keys are non-extractable.
// Should probably make this non-static

@Throws(CryptoException::class)
fun encryptKeyWithJavaKey(
    plaintextKey: ByteArray,
    encryptionKey: Key
): ByteArray {
    try {
        val cipher = Cipher.getInstance(Cryptor.AES_MODE_NO_PADDING)
        val params = IvParameterSpec(fixedIv)
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, params)
        return cipher.doFinal(plaintextKey)
    } catch (e: BadPaddingException) {
        throw CryptoException("Decrypt key with java", e)
    } catch (e: BadPaddingException) {
        throw CryptoException("Decrypt key with java", e)
    } catch (e: InvalidKeyException) {
        throw CryptoException("Decrypt key with java", e)
    }
}

@Throws(CryptoException::class)
fun decryptKeyWithJavaKey(
    encryptedKey: ByteArray,
    encryptionKey: Key
): ByteArray {
    try {
        val cipher = Cipher.getInstance(Cryptor.AES_MODE_NO_PADDING)
        val params = IvParameterSpec(fixedIv)
        cipher.init(Cipher.DECRYPT_MODE, encryptionKey, params)
        return cipher.doFinal(encryptedKey)
    } catch (e: BadPaddingException) {
        throw CryptoException("Decrypt key with java", e)
    } catch (e: BadPaddingException) {
        throw CryptoException("Decrypt key with java", e)
    } catch (e: InvalidKeyException) {
        throw CryptoException("Decrypt key with java", e)
    }
}

fun isAtLeastOreo() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

fun isAtLeastNougat() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

fun readStream(inputStream: InputStream): String = String(inputStream.readBytes())