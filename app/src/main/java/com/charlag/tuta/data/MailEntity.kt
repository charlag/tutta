package com.charlag.tuta.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.IdTuple
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class MailAddressEntity(val name: String, val address: String)

@Entity
data class MailEntity(
    @PrimaryKey val id: Id,
    val listId: Id,
    val confidential: Boolean,
    val differentEnvelopeSender: String?,
    val receivedDate: Date,
    val sendDate: Date,
    val subject: String,
    val unread: Boolean,
    @Embedded
    val sender: MailAddressEntity,
    val toReciipients: List<MailAddressEntity>,
    val ccReciipients: List<MailAddressEntity>,
    val bccReciipients: List<MailAddressEntity>,
    val body: Id,
    val conversationEntry: IdTuple
)