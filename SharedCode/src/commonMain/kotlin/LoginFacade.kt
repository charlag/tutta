package com.charlag.tuta

import com.charlag.tuta.entities.GENERATED_ID_BYTES_LENGTH
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.*
import com.charlag.tuta.network.API
import com.charlag.tuta.network.GroupKeysCache
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CompletableDeferred

data class SessionData(
    val user: User,
    val sessionId: IdTuple,
    val accessToken: String
)

class LoginFacade(
    private val cryptor: Cryptor,
    private val api: API,
    val groupKeysCache: GroupKeysCache
) {
    private var _user: User? = null
    private val _loggedIn = CompletableDeferred<User>()

    val user: User? get() = _user

    suspend fun waitForLogin(): User = _loggedIn.await()

    private suspend fun createAuthVerifier(cryptor: Cryptor, passwordKey: ByteArray): ByteArray =
        cryptor.hash(passwordKey)

    private suspend fun createAuthVerifierAsBase64Url(
        cryptor: Cryptor,
        passwordKey: ByteArray
    ): String {
        val bytes = createAuthVerifier(cryptor, passwordKey)
        return bytes.toBase64().let(::base64ToBase64Url)
    }

    suspend fun createSession(mailAddress: String, passphrase: String): SessionData {
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
        api.accessToken = sessionReturn.accessToken
        val user = api.loadElementEntity<User>(sessionReturn.user)
        groupKeysCache.user = user
        groupKeysCache.cachedKeys[user.userGroup.group.asString()] =
            cryptor.decryptKey(user.userGroup.symEncGKey, passphraseKey)
        _user = user
        _loggedIn.complete(user)
        return SessionData(user, getSessionId(sessionReturn.accessToken), sessionReturn.accessToken)
    }

    suspend fun resumeSession(
        mailAddress: String,
        userId: Id,
        accessToken: String,
        passphrase: String
    ) {
        val (salt) = getSalt(mailAddress)

        val passphraseKey = cryptor.generateKeyFromPassphrase(passphrase, salt)
        api.accessToken = accessToken
        val user = api.loadElementEntity<User>(userId)
        groupKeysCache.user = user
        groupKeysCache.cachedKeys[user.userGroup.group.asString()] =
            cryptor.decryptKey(user.userGroup.symEncGKey, passphraseKey)
        _user = user
        _loggedIn.complete(user)
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
            .let { cryptor.hash(it) }
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