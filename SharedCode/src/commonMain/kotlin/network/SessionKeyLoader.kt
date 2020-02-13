package com.charlag.tuta.network

import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.BucketPermission
import com.charlag.tuta.entities.sys.Group
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.entities.sys.Permission

interface SessionKeyLoader {
    suspend fun loadPermissions(listId: Id): List<Permission>

    suspend fun loadBuckerPermissions(listId: Id): List<BucketPermission>

    suspend fun loadGroup(groupId: Id): Group

    suspend fun updatePermission(
        permissionId: IdTuple,
        bucketPermissionId: IdTuple,
        bucketPermissionOwnerEncSessionKey: ByteArray,
        bucketPermissionEncSessionKey: ByteArray
    )
}