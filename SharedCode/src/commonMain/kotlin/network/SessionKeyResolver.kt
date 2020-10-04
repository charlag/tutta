package com.charlag.tuta.network

import com.charlag.tuta.*
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.TypeModel
import com.charlag.tuta.entities.sys.BucketPermission
import com.charlag.tuta.entities.sys.Group
import com.charlag.tuta.entities.sys.Permission
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

class SessionKeyResolver(
    private val cryptor: Cryptor,
    private val sessionDataProvider: SessionDataProvider
) {

    suspend fun resolveSessionKey(
        typeModel: TypeModel,
        ownerEncSessionKey: ByteArray?,
        ownerGroup: String?,
        permissions: String?,
        loader: SessionKeyLoader
    ): ByteArray? {
        if (!typeModel.encrypted) return null
        val groupKey = ownerGroup?.let { sessionDataProvider.getGroupKey(ownerGroup) }
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
                    sessionDataProvider.getGroupKey(p._ownerGroup.asString()) != null
        }
        if (symmetricPermission != null) {
            val gk = sessionDataProvider.getGroupKey(symmetricPermission._ownerGroup!!.asString())
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
            val group = getGroup(bucketPermission.group, loader)
            val keypair = group.keys[0]
            // decrypt RSA keys
            val bucketPermissionGroupKey = sessionDataProvider.getGroupKey(group._id!!.asString())
                ?: error("No key for ${group._id} ")

            val privKey = cryptor.decryptRsaKey(keypair.symEncPrivKey, bucketPermissionGroupKey)
            val bucketKey = cryptor.rsaDecrypt(bucketPermission.pubEncBucketKey, privKey)
            val sessionKey = cryptor.decryptKey(publicPermission.bucketEncSessionKey, bucketKey)
            val bucketPermissionOwnerGroupKey =
                sessionDataProvider.getGroupKey(bucketPermission._ownerGroup!!.asString())
                    ?: error(
                        "Could not get bucketPermissionOwnerGroupKey for group " +
                                bucketPermission._ownerGroup
                    )
            updateWithSymPermissionKey(
                publicPermission,
                bucketPermission,
                bucketPermissionOwnerGroupKey,
                bucketPermissionGroupKey,
                sessionKey,
                loader
            )
            return sessionKey
        }
    }

    // Groups keys do not change now anyway so it should be okay to cache it.
    // This is not very safe in multi-threading environment, we probably should lock it somehow
    private val groupCache = mutableMapOf<Id, Group>()

    private suspend fun getGroup(id: Id, loader: SessionKeyLoader): Group {
        return this.groupCache[id] ?: loader.loadGroup(id).also { groupCache[id] = it }
    }

    private suspend fun updateWithSymPermissionKey(
        publicPermission: Permission,
        bucketPermission: BucketPermission,
        bucketPermissionOwnerGroupKey: ByteArray,
        bucketPermissionGroupKey: ByteArray,
        sessionKey: ByteArray,
        loader: SessionKeyLoader
    ) {
        // FIXME it's not always correct to do it with the service
        //  it's only okay for updating bucket permissions like mailbody but not for mails
        loader.updatePermission(
            publicPermission._id!!,
            bucketPermission._id!!,
            cryptor.encryptKey(sessionKey, bucketPermissionOwnerGroupKey),
            cryptor.encryptKey(sessionKey, bucketPermissionGroupKey)
        )
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
        this as? String ?: (this as? JsonElement)?.jsonPrimitive?.contentOrNull
}
