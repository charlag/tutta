package com.charlag.tuta

import com.charlag.tuta.entities.GENERATED_ID_BYTES_LENGTH
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.entities.sys.Session
import com.charlag.tuta.entities.sys.User
import kotlinx.io.core.toByteArray

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
    val user: User? get() = _user

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
        val (salt) = api.getSalt(mailAddress)

        val passphraseKey = cryptor.generateKeyFromPassphrase(passphrase, salt)
        val authVerifier = createAuthVerifierAsBase64Url(cryptor, passphraseKey)
        val sessionReturn = api.createSession(mailAddress, authVerifier)
        api.accessToken = sessionReturn.accessToken
        val user = api.loadElementEntity<User>(sessionReturn.user)
        groupKeysCache[user.userGroup.group] =
            cryptor.decryptKey(user.userGroup.symEncGKey, passphraseKey)
        _user = user
        return SessionData(user, getSessionId(sessionReturn.accessToken), sessionReturn.accessToken)
    }

    suspend fun resumeSession(
        mailAddress: String,
        userId: Id,
        accessToken: String,
        passphrase: String
    ) {
        val (salt) = api.getSalt(mailAddress)

        val passphraseKey = cryptor.generateKeyFromPassphrase(passphrase, salt)
        api.accessToken = accessToken
        val user = api.loadElementEntity<User>(userId)
        groupKeysCache[user.userGroup.group] =
            cryptor.decryptKey(user.userGroup.symEncGKey, passphraseKey)
        _user = user
    }

    suspend fun getSessionId(accessToken: String): IdTuple {
        val byteAccessToken = base64UrlToBase64(accessToken).toByteArray()
        val listId = cryptor.hash(byteAccessToken.copyOfRange(0, GENERATED_ID_BYTES_LENGTH))
            .toBase64()
            .let(::base64ToBase64Url)
            .let(::GeneratedId)
        val elementId = cryptor.hash(byteAccessToken.copyOfRange(0, GENERATED_ID_BYTES_LENGTH))
            .toBase64()
            .let(::base64ToBase64Url)
            .let(::GeneratedId)
        return IdTuple(listId, elementId)
    }
}