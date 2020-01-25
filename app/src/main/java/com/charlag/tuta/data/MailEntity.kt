package com.charlag.tuta.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.IdTuple
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class MailAddressEntity(val name: String, val address: String, val contact: String? = null)

@Entity
data class MailEntity(
    @PrimaryKey val id: String,
    val listId: String,
    val _owner: Id,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val _ownerEncSessionKey: ByteArray?,
    val confidential: Boolean,
    val differentEnvelopeSender: String?,
    val listUnsubscribe: Boolean,
    val movedTime: Date?,
    val receivedDate: Date,
    val replyType: Long,
    val sentDate: Date,
    val state: Long,
    val subject: String,
    val trashed: Boolean,
    val unread: Boolean,
    val bccRecipients: List<MailAddressEntity>,
    val ccRecipients: List<MailAddressEntity>,
    val replyTos: List<MailAddressEntity>,
    val sender: MailAddressEntity,
    val toRecipients: List<MailAddressEntity>,
    val attachments: List<IdTuple>,
    val body: Id,
    val conversationEntry: IdTuple,
    val headers: Id?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MailEntity

        if (id != other.id) return false
        if (listId != other.listId) return false
        if (_owner != other._owner) return false
        if (_ownerGroup != other._ownerGroup) return false
        if (_permissions != other._permissions) return false
        if (confidential != other.confidential) return false
        if (differentEnvelopeSender != other.differentEnvelopeSender) return false
        if (listUnsubscribe != other.listUnsubscribe) return false
        if (movedTime != other.movedTime) return false
        if (receivedDate != other.receivedDate) return false
        if (replyType != other.replyType) return false
        if (sentDate != other.sentDate) return false
        if (state != other.state) return false
        if (subject != other.subject) return false
        if (trashed != other.trashed) return false
        if (unread != other.unread) return false
        if (bccRecipients != other.bccRecipients) return false
        if (ccRecipients != other.ccRecipients) return false
        if (replyTos != other.replyTos) return false
        if (sender != other.sender) return false
        if (toRecipients != other.toRecipients) return false
        if (attachments != other.attachments) return false
        if (body != other.body) return false
        if (conversationEntry != other.conversationEntry) return false
        if (headers != other.headers) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + listId.hashCode()
        result = 31 * result + _owner.hashCode()
        result = 31 * result + (_ownerGroup?.hashCode() ?: 0)
        result = 31 * result + _permissions.hashCode()
        result = 31 * result + confidential.hashCode()
        result = 31 * result + (differentEnvelopeSender?.hashCode() ?: 0)
        result = 31 * result + listUnsubscribe.hashCode()
        result = 31 * result + (movedTime?.hashCode() ?: 0)
        result = 31 * result + receivedDate.hashCode()
        result = 31 * result + replyType.hashCode()
        result = 31 * result + sentDate.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + subject.hashCode()
        result = 31 * result + trashed.hashCode()
        result = 31 * result + unread.hashCode()
        result = 31 * result + bccRecipients.hashCode()
        result = 31 * result + ccRecipients.hashCode()
        result = 31 * result + replyTos.hashCode()
        result = 31 * result + sender.hashCode()
        result = 31 * result + toRecipients.hashCode()
        result = 31 * result + attachments.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + conversationEntry.hashCode()
        result = 31 * result + (headers?.hashCode() ?: 0)
        return result
    }
}