package com.charlag.tuta.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
@ForeignKey(
    entity = MailFolderEntity::class,
    parentColumns = ["id"],
    childColumns = ["mails"],
    onDelete = ForeignKey.CASCADE
)
class MailFolderCounterEntity(
    @PrimaryKey
    val mailListId: String,
    val counter: Long
)