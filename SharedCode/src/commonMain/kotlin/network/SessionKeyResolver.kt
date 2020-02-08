package com.charlag.tuta.network

import com.charlag.tuta.*
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.TypeModel
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull

class SessionKeyResolver(
    private val cryptor: Cryptor,
    private val groupKeysCache: GroupKeysCache
) {

    suspend fun resolveSessionKey(
        typeModel: TypeModel,
        ownerEncSessionKey: ByteArray?,
        ownerGroup: String?,
        permissions: String?,
        loader: SessionKeyLoader
    ): ByteArray? {
        if (!typeModel.encrypted) return null
        val groupKey = ownerGroup?.let { groupKeysCache.getGroupKey(ownerGroup) }
        if (ownerEncSessionKey != null && groupKey != null) {
            try {
                return cryptor.decryptKey(ownerEncSessionKey, groupKey)
            } catch (e: CryptoException) {
                println("Failed to decrypt ownerEncSessionKey $ownerEncSessionKey with $groupKey, $e")
                throw e
            }
        }
        permissions ?: return null
        val listPermissions =
            loader.loadPermissions(GeneratedId(permissions))
        val symmetricPermission = listPermissions.find { p ->
            (p.type == PermissionType.PublicSymemtric.value ||
                    p.type == PermissionType.Symmetric.value) &&
                    p._ownerGroup != null &&
                    groupKeysCache.getGroupKey(p._ownerGroup.asString()) != null
        }
        if (symmetricPermission != null) {
            val gk = groupKeysCache.getGroupKey(symmetricPermission._ownerGroup!!.asString())
            return cryptor.decryptKey(symmetricPermission._ownerEncSessionKey!!, gk!!)
        }
        val publicPermission =
            listPermissions.find { lp ->
                lp.type == PermissionType.Public.value || lp.type == PermissionType.External.value
            } ?: error("Could not find permission")

        val bucketPermissions =
            loader.loadBuckerPermissions(publicPermission.bucket!!.bucketPermissions)
        // find the bucket permission with the same group as the permission and public type
        val bucketPermission = bucketPermissions.find { bp ->
            (bp.type == BucketPermissionType.Public.value || bp.type == BucketPermissionType.External.value) &&
                    publicPermission._ownerGroup == bp._ownerGroup
        } ?: error("no corresponding bucket permission found")

        if (bucketPermission.type == BucketPermissionType.External.value) {
            error("External permissions are not implemented")
        } else {
            bucketPermission.pubEncBucketKey
                ?: error("PubEncBucketKey is not defined for BucketPermission $bucketPermissions")
            publicPermission.bucketEncSessionKey
                ?: error("BucketEncSessionKey is not defined for $symmetricPermission")
            val group = loader.loadGroup(bucketPermission.group)
            val keypair = group.keys[0]
            // decrypt RSA keys
            val bucketPermissionGroupKey = groupKeysCache.getGroupKey(group._id.asString())
                ?: error("No key for ${group._id} ")

            val privKey = cryptor.decryptRsaKey(keypair.symEncPrivKey, bucketPermissionGroupKey)
            val bucketKey = cryptor.rsaDecrypt(bucketPermission.pubEncBucketKey, privKey)
            val sessionKey = cryptor.decryptKey(publicPermission.bucketEncSessionKey, bucketKey)
            return sessionKey
            // TODO: _updateWithSymPermissionKey
//            val bucketPermissionOwnerGroupKey =
//                groupKeysCache.getGroupKey(bucketPermission._ownerGroup!!.asString())
//            val buckePermissionGroupKey =
//                groupKeysCache.getGroupKey(bucketPermission.group.asString())

        }
    }

    suspend fun resolveSessionKey(
        typeModel: TypeModel,
        instance: Map<String, Any?>,
        loader: SessionKeyLoader
    ): ByteArray? {
        val ownerEncSessionKey = instance["_ownerEncSessionKey"]
        val sessionKeyBytes = if (ownerEncSessionKey != null && ownerEncSessionKey is ByteArray) {
            ownerEncSessionKey
        } else {
            ownerEncSessionKey.stringOrJsonContent()?.let(::base64ToBytes)
        }
        try {
            return this.resolveSessionKey(
                typeModel,
                sessionKeyBytes,
                instance["_ownerGroup"].stringOrJsonContent(),
                instance["_permissions"].stringOrJsonContent(),
                loader
            )
        } catch (e: CryptoException) {
            println(
                "Failed resolve session key of instance $instance with key of length " +
                        "${sessionKeyBytes?.size}, $e"
            )
            throw e
        }
    }

    private fun Any?.stringOrJsonContent() =
        this as? String ?: (this as? JsonElement)?.contentOrNull
}
