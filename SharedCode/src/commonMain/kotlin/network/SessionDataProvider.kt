package com.charlag.tuta.network

import com.charlag.tuta.Cryptor
import com.charlag.tuta.SessionData
import com.charlag.tuta.decryptKey

interface SessionDataProvider {
    val accessToken: String?
    suspend fun getGroupKey(groupId: String): ByteArray?
    fun setAccessToken(accessToken: String)
    fun setSessionData(sessionData: SessionData)
}

class UserSessionDataProvider(
    private val cryptor: Cryptor
) : SessionDataProvider {
    private val cachedKeys = mutableMapOf<String, ByteArray>()
    private var sessionData: SessionData? = null
    override var accessToken: String? = null
        private set

    override fun setAccessToken(accessToken: String) {
        check(this.accessToken == null)
        this.accessToken = accessToken
    }

    override fun setSessionData(sessionData: SessionData) {
        check(this.sessionData == null)
        this.sessionData = sessionData
        cachedKeys[sessionData.user.userGroup.group.asString()] = sessionData.userGroupKey
    }

    override suspend fun getGroupKey(groupId: String): ByteArray? {
        val sessionData = this.sessionData
        return cachedKeys[groupId]
            ?: sessionData?.user?.memberships?.find { it.group.asString() == groupId }
                ?.let { membership ->
                    cryptor.decryptKey(membership.symEncGKey, sessionData.userGroupKey)
                }
    }
}