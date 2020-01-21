package com.charlag.tuta

import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.Group
import com.charlag.tuta.entities.sys.GroupInfo
import com.charlag.tuta.entities.sys.GroupMembership
import com.charlag.tuta.entities.sys.User
import com.charlag.tuta.entities.tutanota.*
import io.ktor.http.HttpMethod

data class RecipientInfo(val name: String, val mailAddress: String)


class MailFacade(val api: API, val cryptor: Cryptor) {

    suspend fun createDraft(
        user: User,
        subject: String,
        body: String,
        senderAddress: String,
        senderName: String,
        toRecipients: List<RecipientInfo>,
        ccRecipients: List<RecipientInfo>,
        bccRecipients: List<RecipientInfo>,
        conversationType: ConversationType,
        previousMessageId: Id?,
//        attachments: List<TutanotaFile | DataFile | FileReference>,
        confidential: Boolean,
        replyTos: List<RecipientInfo>
    ): Mail {
        val mailGroupId = getMailGroupIdForMailAddress(user, senderAddress).asString()
        val userGroupKey = api.groupKeysCache.getGroupKey(user.userGroup.group.asString()) ?: error(
            "Could not get user group key"
        )
        val mailGroupKey =
            api.groupKeysCache.getGroupKey(mailGroupId) ?: error("Could not get mail group key")

        val sk = cryptor.aes128RandomKey()
        val draftData = DraftData(
            _id = generateId(cryptor),
            subject = subject,
            bodyText = body,
            senderMailAddress = senderAddress,
            senderName = senderName,
            confidential = confidential,
            toRecipients = recipientInfoToDraftRecipient(toRecipients),
            ccRecipients = recipientInfoToDraftRecipient(ccRecipients),
            bccRecipients = recipientInfoToDraftRecipient(bccRecipients),
            replyTos = replyTos.map { recipient ->
                EncryptedMailAddress(
                    address = recipient.mailAddress,
                    name = recipient.name
                )
            },
            addedAttachments = listOf(),
            removedAttachments = listOf()
        )
        val service = DraftCreateData(
            previousMessageId = previousMessageId?.asString(),
            conversationType = conversationType.value,
            ownerEncSessionKey = cryptor.encryptKey(
                plaintextKey = sk,
                encryptionKey = mailGroupKey
            ),
            symEncSessionKey = cryptor.encryptKey(
                plaintextKey = sk,
                encryptionKey = userGroupKey
            ),
            draftData = draftData
        )
        val draftCreateReturn = api.serviceRequest(
            "tutanota", "draftservice", HttpMethod.Post, service, DraftCreateReturn::class, null, sk
        )
        return api.loadListElementEntity(draftCreateReturn.draft)
    }

    suspend fun getMailGroupIdForMailAddress(user: User, mailAddress: String): Id {
        val mailMemberips = getUserGroupMemberships(user, GroupType.Mail)
        val filteredMemberships = mailMemberips.filter { groupMembership ->
            val mailGroup = api.loadElementEntity<Group>(groupMembership.group)
            when (mailGroup.user) {
                null -> {
                    val mailgroupInfo =
                        api.loadListElementEntity<GroupInfo>(groupMembership.groupInfo)
                    getEnabledMailAddressesForGroupInfo(mailgroupInfo).contains(mailAddress)
                }
                user._id -> {
                    val userGroupInfo =
                        api.loadListElementEntity<GroupInfo>(user.userGroup.groupInfo)
                    getEnabledMailAddressesForGroupInfo(userGroupInfo).contains(mailAddress)
                }
                else -> {
                    false
                }
            }
        }
        return filteredMemberships[0].group
    }


    private fun recipientInfoToDraftRecipient(toRecipients: List<RecipientInfo>): List<DraftRecipient> {
        return toRecipients.map { ri ->
            DraftRecipient(
                _id = generateId(cryptor),
                mailAddress = ri.mailAddress,
                name = ri.name
            )
        }
    }

}

fun getUserGroupMemberships(user: User, groupType: GroupType): List<GroupMembership> {
    return if (groupType === GroupType.User) {
        listOf(user.userGroup)
    } else {
        user.memberships.filter { it.groupType == groupType.value }
    }
}

fun getEnabledMailAddressesForGroupInfo(groupInfo: GroupInfo): List<String> {
    val addresses = groupInfo.mailAddressAliases.filter { it.enabled }
        .mapTo(mutableListOf()) { it.mailAddress }
    if (groupInfo.mailAddress != null && !addresses.contains(groupInfo.mailAddress)) {
        addresses.add(groupInfo.mailAddress)
    }
    return addresses
}

fun generateId(cryptor: Cryptor) =
    GeneratedId(base64ToBase64Url(cryptor.generateRandomData(4).toBase64()))
