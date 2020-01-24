package com.charlag.tuta.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.charlag.tuta.entities.Id

@Entity
data class MailFolderEntity(
    @PrimaryKey
    val id: String,
    val listId: String,
    val folderType: Long,
    val name: String,
    val mails: Id
)