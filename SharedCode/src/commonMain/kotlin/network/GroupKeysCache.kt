package com.charlag.tuta.network

import com.charlag.tuta.Cryptor
import com.charlag.tuta.SessionData
import com.charlag.tuta.decryptKey

interface GroupKeysCache {
    val accessToken: String?
    suspend fun getGroupKey(groupId: String): ByteArray?
    fun stage1(accessToken: String)
    fun stage2(sessionData: SessionData)
}


class UserGroupKeysCache(
    private val cryptor: Cryptor
) : GroupKeysCache {
    private val cachedKeys = mutableMapOf<String, ByteArray>()
    private var sessionData: SessionData? = null
    override var accessToken: String? = null
        private set

    override fun stage1(accessToken: String) {
        this.accessToken = accessToken
    }

    override fun stage2(sessionData: SessionData) {
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