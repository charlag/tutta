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
import kotlinx.serialization.json.jsonPrimitive

enum class SessionType {
    EPHEMERAL,
    PERSISTENT,
}

@Serializable
data class CreateSessionResult(
    val userId: Id,
    val accessToken: String,
    val passphraseKey: ByteArray,
    val encryptedPassword: ByteArray?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CreateSessionResult

        if (userId != other.userId) return false
        if (accessToken != other.accessToken) return false
        if (!encryptedPassword.contentEquals(other.encryptedPassword)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + accessToken.hashCode()
        result = 31 * result + encryptedPassword.contentHashCode()
        return result
    }
}

data class ResumeSessionResult(
    val userId: Id,
    val passphraseKey: ByteArray,
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
        sessionType: SessionType,
        onSecondFactorPending: SecondFactorCallback,
    ): CreateSessionResult {
        val salt = getSalt(mailAddress).salt

        val passphraseKey = cryptor.generateKeyFromPassphrase(passphrase, salt)
        val authVerifier = createAuthVerifierAsBase64Url(cryptor, passphraseKey)
        val accessKey =
            if (sessionType == SessionType.PERSISTENT) cryptor.aes128RandomKey() else null
        val postData = CreateSessionData(
            mailAddress = mailAddress,
            authVerifier = authVerifier,
            clientIdentifier = "Multiplatform test",
            accessKey = accessKey,
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
        return CreateSessionResult(
            userId = sessionReturn.user,
            accessToken = sessionReturn.accessToken,
            passphraseKey = passphraseKey,
            encryptedPassword = accessKey?.let { key -> cryptor.encryptString(passphrase, key) },
        )
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

    /**
     * Access token must be already set!
     */
    suspend fun resumeSession(
        mailAddress: String,
        accessToken: String,
        encryptedPassword: ByteArray,
    ): ResumeSessionResult {
        val sessionData = loadSessionData(accessToken)
        val accessKey = sessionData.accessKey
        val passphrase = cryptor.aesDecrypt(encryptedPassword, accessKey, usePadding = true)
            .data
            .decodeToString()
        val salt = getSalt(mailAddress).salt
        val pwKey = cryptor.generateKeyFromPassphrase(passphrase, salt)
        return ResumeSessionResult(sessionData.userId, pwKey)
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

    private data class SessionData(val userId: Id, val accessKey: ByteArray)

    /**
     * Access token must be already set.
     *
     * We can't load real Session because some parts of it are encrypted so our normal API
     * machinery will try to decrypt it to map it to instance. We just want unencrypted parts so we
     * take apart json manually to get what we need.
     */
    private suspend fun loadSessionData(accessTokenB64Url: String): SessionData {
        val sessionId = getSessionId(accessTokenB64Url)
        val json = api.loadListElementEntityRaw(sessionId, Session::class)
        val userId = json["user"]!!.jsonPrimitive.content
        val accessKey = base64ToBytes(json["accessKey"]!!.jsonPrimitive.content)
        return SessionData(GeneratedId(userId), accessKey)
    }
}