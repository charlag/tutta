package com.charlag.tuta

import com.charlag.tuta.entities.GENERATED_ID_BYTES_LENGTH
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.IdTuple
import com.charlag.tuta.entities.sys.*
import com.charlag.tuta.network.API
import io.ktor.client.features.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

class SessionData(
    val user: User,
    val accessToken: String,
    val userGroupKey: ByteArray
)

@Serializable
data class CreateSessionResult(
    val userId: Id,
    val passphraseKey: ByteArray,
    val accessToken: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CreateSessionResult

        if (userId != other.userId) return false
        if (!passphraseKey.contentEquals(other.passphraseKey)) return false
        if (accessToken != other.accessToken) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + passphraseKey.contentHashCode()
        result = 31 * result + accessToken.hashCode()
        return result
    }
}

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
        val salt = getSalt(mailAddress).salt

        val passphraseKey = cryptor.generateKeyFromPassphrase(passphrase, salt)
        val authVerifier = createAuthVerifierAsBase64Url(cryptor, passphraseKey)
        val postData = CreateSessionData(
            mailAddress = mailAddress,
            authVerifier = authVerifier,
            clientIdentifier = "Multiplatform test",
            accessKey = null,
            authToken = null,
            recoverCodeVerifier = null,
            user = null
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
                } catch (e: Throwable) {
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
        val salt = getSalt(mailAddress).salt
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
            "sys", "saltservice", HttpMethod.Get, SaltData(null, mailAddress),
            SaltReturn::class
        )
    }
}