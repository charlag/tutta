package com.charlag.tuta.network

import com.charlag.tuta.Cryptor
import com.charlag.tuta.decryptKey
import com.charlag.tuta.entities.sys.User

class GroupKeysCache(private val cryptor: Cryptor) {
    var user: User? = null
    val cachedKeys = mutableMapOf<String, ByteArray>()

    suspend fun getGroupKey(groupId: String): ByteArray? {
        val user = this.user
        return cachedKeys[groupId]
            ?: if (user != null) {
                val membership = user.memberships.find { it.group.asString() == groupId }
                if (membership != null) {
                    val userGroupKey = cachedKeys[user.userGroup.group.asString()]!!
                    cryptor.decryptKey(membership.symEncGKey, userGroupKey)
                } else {
                    null
                }
            } else {
                null
            }
    }
}