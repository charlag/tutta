package com.charlag.tuta.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.IdTuple

@Entity
data class MailFolderEntity(
    @PrimaryKey
    val id: String,
    val listId: String,
    val folderType: Long,
    val name: String,
    val mails: Id,
    val ownerEncSessionKey: ByteArray?,
    val ownerGroup: Id?,
    val permissions: Id,
    val parentFolder: IdTuple?,
    val subFolders: Id
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MailFolderEntity

        if (id != other.id) return false
        if (listId != other.listId) return false
        if (folderType != other.folderType) return false
        if (name != other.name) return false
        if (mails != other.mails) return false
        if (ownerGroup != other.ownerGroup) return false
        if (permissions != other.permissions) return false
        if (subFolders != other.subFolders) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + listId.hashCode()
        result = 31 * result + folderType.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + mails.hashCode()
        result = 31 * result + (ownerGroup?.hashCode() ?: 0)
        result = 31 * result + permissions.hashCode()
        result = 31 * result + subFolders.hashCode()
        return result
    }
}

data class MailFolderWithCounter(
    @Embedded
    val folder: MailFolderEntity,
    val counter: Long
)