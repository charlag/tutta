package com.charlag.tuta

import com.charlag.tuta.entities.GENERATED_ID_BYTES_LENGTH
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.IdTuple
import com.charlag.tuta.entities.sys.*
import com.charlag.tuta.network.API
import io.ktor.client.features.ClientRequestException
import io.ktor.http.HttpMethod
import kotlin.Exception

class SessionData(
    val user: User,
    val accessToken: String,
    val userGroupKey: ByteArray
)

data class CreateSessionResult(
    val userId: Id,
    val passphraseKey: ByteArray,
    val accessToken: String
)

typealias SecondFactorCallback = (sessionId: IdTuple) -> Unit

class LoginFacade(
    private val cryptor: Cryptor,
    private val api: API
) {
    private suspend fun createAuthVerifier(cryptor: Cryptor, passwordKey: ByteArray): ByteArray =
        cryptor.sha256hash(passwordKey)

    private suspend fun createAuthVerifierAsBase64Url(
        cryptor: Cryptor,
        passwordKey: ByteArray
    ): String {
        val bytes = createAuthVerifier(cryptor, passwordKey)
        return bytes.toBase64().let(::base64ToBase64Url)
    }

    suspend fun createSession(
        mailAddress: String,
        passphrase: String,
        onSecondFactorPending: SecondFactorCallback
    ): CreateSessionResult {
        val (salt) = getSalt(mailAddress)

        val passphraseKey = cryptor.generateKeyFromPassphrase(passphrase, salt)
        val authVerifier = createAuthVerifierAsBase64Url(cryptor, passphraseKey)
        val postData = CreateSessionData(
            mailAddress = mailAddress,
            authVerifier = authVerifier,
            clientIdentifier = "Multiplatform test"
        )
        val sessionReturn = api.serviceRequest(
            "sys", "sessionService", HttpMethod.Post, postData,
            CreateSessionReturn::class
        )
        if (sessionReturn.challenges.isNotEmpty()) {
            onSecondFactorPending(getSessionId(sessionReturn.accessToken))
            for (i in 0..10) {
                try {
                    val authGetReturn = api.serviceRequest(
                        "sys",
                        "secondfactorauthservice",
                        HttpMethod.Get,
                        SecondFactorAuthGetData(accessToken = sessionReturn.accessToken),
                        SecondFactorAuthGetReturn::class
                    )
                    if (authGetReturn.secondFactorPending) {
                        continue
                    }
                } catch (e: Exception) {
                    if (e is ClientRequestException) {
                        throw e
                    }
                }
            }
        }
        return CreateSessionResult(sessionReturn.user, passphraseKey, sessionReturn.accessToken)
    }

    suspend fun submitTOTPLogin(sessionId: IdTuple, code: Long) {
        api.serviceRequestVoid(
            "sys", "secondfactorauthservice", HttpMethod.Post, SecondFactorAuthData(
                otpCode = code,
                type = SecondFactorType.TOTP.raw,
                session = sessionId,
                u2f = null
            )
        )
    }

    suspend fun resumeSession(
        mailAddress: String,
        passphrase: String
    ): ByteArray {
        val (salt) = getSalt(mailAddress)
        return cryptor.generateKeyFromPassphrase(passphrase, salt)
    }

    suspend fun getUserGroupKey(user: User, passphraseKey: ByteArray): ByteArray {
        return cryptor.decryptKey(user.userGroup.symEncGKey, passphraseKey)
    }

    suspend fun deleteSession(accessToken: String) {
        return api.deleteListElementEntity(Session::class, getSessionId(accessToken))
    }

    suspend fun getSessionId(accessToken: String): IdTuple {
        val byteAccessToken = base64ToBytes(base64UrlToBase64(accessToken))
        // Be *extremely* mindful with changes for these two: has, length, format all differ
        val listId = byteAccessToken.copyOfRange(0, GENERATED_ID_BYTES_LENGTH)
            .toBase64()
            .let(::base64ToBase64Ext)
            .let(::GeneratedId)

        val elementId = byteAccessToken.copyOfRange(GENERATED_ID_BYTES_LENGTH, byteAccessToken.size)
            .let { cryptor.sha256hash(it) }
            .toBase64()
            .let(::base64ToBase64Url)
            .let(::GeneratedId)
        return IdTuple(listId, elementId)
    }

    private suspend fun getSalt(mailAddress: String): SaltReturn {
        return api.serviceRequest(
            "sys", "saltservice", HttpMethod.Get, SaltData(mailAddress),
            SaltReturn::class
        )
    }
}