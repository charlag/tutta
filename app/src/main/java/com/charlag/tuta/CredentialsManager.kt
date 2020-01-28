package com.charlag.tuta

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class CredentialsManager {

    fun register() {
        generateSecretKey(
            KeyGenParameterSpec.Builder(
                KEY_NAME,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .setInvalidatedByBiometricEnrollment(true)
                .build()
        )
    }

    fun prepareCipher(iv: ByteArray?): Cipher {
        return getCipher().apply {
            val mode = if (iv == null) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE
            init(mode, getSecretKey(), iv?.let(::IvParameterSpec))
        }
    }

    private fun generateSecretKey(keyGenParameterSpec: KeyGenParameterSpec) {
        KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
        ).apply {
            init(keyGenParameterSpec)
            generateKey()
        }
    }

    private fun getSecretKey(): SecretKey? {
        return KeyStore.getInstance("AndroidKeyStore").run {
            load(null)
            getKey(KEY_NAME, null) as SecretKey
        }
    }

    private fun getCipher(): Cipher = Cipher.getInstance(
        KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7
    )

    companion object {
        private const val KEY_NAME = "credentials_key"
    }
}