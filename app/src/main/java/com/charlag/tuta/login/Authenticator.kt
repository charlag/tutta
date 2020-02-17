package com.charlag.tuta.login

import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.charlag.tuta.BiometricCredentialsManager
import com.charlag.tuta.Cryptor
import com.charlag.tuta.aes128RandomKey
import com.charlag.tuta.notifications.AndroidKeyStoreFacade
import com.charlag.tuta.user.SessionStore
import kotlinx.coroutines.CompletableDeferred
import javax.crypto.Cipher

internal sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Failure(val message: String) : AuthResult<Nothing>()
    object Cancelled : AuthResult<Nothing>()
}

private typealias GetKeyResult = AuthResult<ByteArray>
private typealias SystemScreen = FragmentActivity
private typealias PutKeyResult = AuthResult<Unit>

internal interface Authenticator {
    suspend fun getDeviceKey(systemScreen: SystemScreen): GetKeyResult
    suspend fun changeBiometricsPref(
        systemScreen: SystemScreen,
        biometricsDesired: Boolean
    ): PutKeyResult
}

internal class RealAuthenticator(
    private val sessionStore: SessionStore,
    private val keyStoreFacade: AndroidKeyStoreFacade,
    private val cryptor: Cryptor
) : Authenticator {
    private fun authMechanism(biometrics: Boolean): AuthMechanism = if (biometrics) {
        BiometricAuthMechanism()
    } else {
        AndroidKeystoreAuthMechanism(keyStoreFacade)
    }

    override suspend fun changeBiometricsPref(
        systemScreen: SystemScreen,
        biometricsDesired: Boolean
    ): PutKeyResult {
        if (sessionStore.usingBiometrics == biometricsDesired) {
            return AuthResult.Success(Unit)
        }
        return getDeviceKey(systemScreen)
            .flatMapSuccess<ByteArray, ByteArray> { plaintextKey ->
                authMechanism(biometricsDesired).encryptKey(systemScreen, plaintextKey)
            }.mapSuccess { encKey ->
                sessionStore.storeDeviceKey(encKey, biometricsDesired)
            }
    }

    override suspend fun getDeviceKey(systemScreen: SystemScreen): GetKeyResult {
        val existingKey = sessionStore.getDeviceKey()
        return if (existingKey == null) {
            val newKey = cryptor.aes128RandomKey()
            authMechanism(sessionStore.usingBiometrics)
                .encryptKey(systemScreen, newKey)
                .mapSuccess { encKey ->
                    sessionStore.storeDeviceKey(encKey, sessionStore.usingBiometrics)
                    newKey
                }
        } else {
            authMechanism(sessionStore.usingBiometrics).decryptKey(systemScreen, existingKey)
        }
    }
}

private interface AuthMechanism {
    suspend fun encryptKey(systemScreen: SystemScreen, plaintext: ByteArray): GetKeyResult
    suspend fun decryptKey(systemScreen: SystemScreen, encrypted: ByteArray): GetKeyResult
}

private class BiometricAuthMechanism : AuthMechanism {
    private val credentialsManager = BiometricCredentialsManager()

    override suspend fun encryptKey(
        systemScreen: SystemScreen,
        plaintext: ByteArray
    ): GetKeyResult {
        return authenticateCipher(
            systemScreen,
            credentialsManager.prepareCipher(null)
        ).mapSuccess { cipher ->
            val result = cipher.doFinal(plaintext)!!
            cipher.iv + result
        }
    }

    override suspend fun decryptKey(
        systemScreen: SystemScreen,
        encrypted: ByteArray
    ): GetKeyResult {
        val iv = encrypted.copyOfRange(0, 16)
        val encDeviceKeyWithoutIv = encrypted.copyOfRange(16, encrypted.size)

        return authenticateCipher(
            systemScreen,
            credentialsManager.prepareCipher(iv)
        ).mapSuccess { cipher ->
            cipher.doFinal(encDeviceKeyWithoutIv)
        }
    }

    private suspend fun authenticateCipher(
        systemScreen: SystemScreen,
        cipher: Cipher
    ): AuthResult<Cipher> {
        val deferred = CompletableDeferred<AuthResult<Cipher>>()
        val prompt = BiometricPrompt(
            systemScreen,
            ContextCompat.getMainExecutor(systemScreen),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    deferred.complete(AuthResult.Failure(errString.toString()))
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    deferred.complete(AuthResult.Success(result.cryptoObject!!.cipher!!))
                }

                override fun onAuthenticationFailed() {
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login")
            .setNegativeButtonText("Cancel")
            .build()

        prompt.authenticate(
            promptInfo,
            BiometricPrompt.CryptoObject(cipher)
        )
        return deferred.await()
    }
}

private class AndroidKeystoreAuthMechanism(
    private val keyStoreFacade: AndroidKeyStoreFacade
) : AuthMechanism {
    override suspend fun encryptKey(
        systemScreen: SystemScreen,
        plaintext: ByteArray
    ): GetKeyResult {
        return keyStoreFacade.encryptKey(plaintext).let { AuthResult.Success(it) }
    }

    override suspend fun decryptKey(
        systemScreen: SystemScreen,
        encrypted: ByteArray
    ): GetKeyResult {
        return keyStoreFacade.decryptKey(encrypted).let { AuthResult.Success(it) }
    }
}


internal inline fun <T, R> AuthResult<T>.mapSuccess(mapper: (T) -> R): AuthResult<R> = when (this) {
    is AuthResult.Success -> AuthResult.Success(mapper(this.data))
    // unpacking cases manually because compiler is not smart enough to understand that
    // AuthResult<T> == AuthResult<R> in these cases on its own.
    // It's fine, little compiler, you will get there one day.
    is AuthResult.Failure -> this
    is AuthResult.Cancelled -> this
}

internal inline fun <T, R> AuthResult<T>.flatMapSuccess(mapper: (T) -> AuthResult<T>) =
    when (this) {
        is AuthResult.Success -> mapper(this.data)
        is AuthResult.Failure -> this
        is AuthResult.Cancelled -> this
    }

