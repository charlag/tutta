package com.charlag.tuta

import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.*
import com.charlag.tuta.entities.tutanota.*
import com.charlag.tuta.network.API
import com.charlag.tuta.network.GroupKeysCache
import com.charlag.tuta.network.SessionKeyResolver
import io.ktor.client.features.ClientRequestException
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

enum class RecipientType(val raw: Int) {
    UNKNOWN(0),
    INTENRAL(1),
    EXTERNAL(2);

    companion object {
        fun fromRaw(raw: Int): RecipientType {
            return when (raw) {
                INTENRAL.raw -> INTENRAL
                EXTERNAL.raw -> EXTERNAL
                else -> UNKNOWN
            }
        }
    }
}

@Serializable
data class RecipientInfo(val name: String, val mailAddress: String, val type: RecipientType)

class MailFacade(
    private val api: API,
    private val cryptor: Cryptor,
    private val keyResolver: SessionKeyResolver
) {

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
        files: List<UploadedFile>,
        confidential: Boolean,
        replyTos: List<RecipientInfo>
    ): Mail {
        val mailGroupId = getMailGroupIdForMailAddress(user, senderAddress).asString()
        val userGroupKey = api.groupKeysCache.getGroupKey(user.userGroup.group.asString())
            ?: error("Could not get user group key")
        val mailGroupKey =
            api.groupKeysCache.getGroupKey(mailGroupId) ?: error("Could not get mail group key")


        // For now we assume that all files are new
        val draftAttachments = prepareAttachments(files, mailGroupKey)

        val sk = cryptor.aes128RandomKey()
        val draftData = DraftData(
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
            addedAttachments = draftAttachments,
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

    suspend fun updateDraft(
        user: User,
        subject: String,
        body: String,
        senderAddress: String,
        senderName: String,
        toRecipients: List<RecipientInfo>,
        ccRecipients: List<RecipientInfo>,
        bccRecipients: List<RecipientInfo>,
        uploadedFiles: List<UploadedFile>,
        remoteFiles: List<IdTuple>,
        confidential: Boolean,
        draft: Mail // do we really need Mail instance here?
    ): Mail {
        val mailGroupId = getMailGroupIdForMailAddress(user, senderAddress)
        val mailGroupKey =
            api.groupKeysCache.getGroupKey(mailGroupId.asString())
                ?: error("Could not get mail group key")
        val sessionKey = cryptor.decryptKey(draft._ownerEncSessionKey!!, mailGroupKey)

        val requestData = DraftUpdateData(
            _format = 0,
            draftData = DraftData(
                bodyText = body,
                confidential = confidential,
                senderMailAddress = senderAddress,
                senderName = senderName,
                subject = subject,
                addedAttachments = prepareAttachments(uploadedFiles, mailGroupKey),
                removedAttachments = getRemovedAttachments(remoteFiles, draft.attachments),
                toRecipients = recipientInfoToDraftRecipient(toRecipients),
                ccRecipients = recipientInfoToDraftRecipient(ccRecipients),
                bccRecipients = recipientInfoToDraftRecipient(bccRecipients),
                replyTos = draft.replyTos
            ),
            draft = draft._id
        )
        api.serviceRequestVoid(
            "tutanota",
            "draftservice",
            HttpMethod.Put,
            requestData,
            null,
            sessionKey
        )
        return api.loadListElementEntity(draft._id)
    }

    private suspend fun prepareAttachments(
        files: List<UploadedFile>,
        mailGroupKey: ByteArray
    ): List<DraftAttachment> {
        return files.map { attachment ->
            val fileSessionKey = attachment.fileSessionKey
            DraftAttachment(
                newFile = NewDraftAttachment(
                    encCid = null,
                    encFileName = cryptor.encryptString(attachment.name, fileSessionKey),
                    encMimeType = cryptor.encryptString(attachment.mimeType, fileSessionKey),
                    fileData = attachment.fileDataId
                ),
                ownerEncFileSessionKey = cryptor.encryptKey(fileSessionKey, mailGroupKey),
                existingFile = null
            )
        }
    }

    private fun getRemovedAttachments(
        files: List<IdTuple>,
        existingFileIds: List<IdTuple>
    ): List<IdTuple> {
        return existingFileIds - files
    }

    suspend fun sendDraft(
        user: User, draft: Mail, recipientInfos: List<RecipientInfo>, language: String
    ) {
        // We don't need a user currently but we'll need them for external secure mails
        val bucketKey = cryptor.aes128RandomKey()
        // Skipping attachments for now
        val mailTypeModel = typemodelMap.getValue(Mail::class.noReflectionName).typemodel
        // Get the current session key of the email. Should be just _ownerEncSessionKey.
        val resolvedMailKey = keyResolver.resolveSessionKey(
            mailTypeModel,
            draft._ownerEncSessionKey,
            draft._ownerGroup?.asString(),
            draft._permissions.asString(),
            api
        ) ?: error("Could not resolve draft session key $draft")

        val internalRecipientKeyData: List<InternalRecipientKeyData>
        val bucketEncMailSessionKey: ByteArray?
        val sessionKeyToPass: ByteArray?
        if (draft.confidential) {
            // If it's confidential email, we do public encryption of the session key.
            bucketEncMailSessionKey = cryptor.encryptKey(resolvedMailKey, bucketKey)
            internalRecipientKeyData = recipientInfos.map {
                encryptBucketKeyForInternalRecipient(bucketKey, it.mailAddress)
                    ?: error("Did not find public key for ${it.mailAddress}")
            }
            sessionKeyToPass = null
        } else {
            // If it's non/confidential email, we just hand session key to the server so it can
            // send it to the external recipients
            bucketEncMailSessionKey = null
            internalRecipientKeyData = listOf()
            sessionKeyToPass = resolvedMailKey
        }

        val attachmentKeyData = draft.attachments.map { fileId ->
            val file = api.loadListElementEntity<File>(fileId)
            val fileSessionKey = keyResolver.resolveSessionKey(
                typemodelMap.getValue(File::class.noReflectionName).typemodel,
                file._ownerEncSessionKey,
                file._ownerGroup?.asString(),
                file._permissions.asString(),
                api
            ) ?: error("Count not resolve file key $fileId")
            if (draft.confidential) {
                AttachmentKeyData(
                    bucketEncFileSessionKey = cryptor.encryptKey(fileSessionKey, bucketKey),
                    file = fileId,
                    fileSessionKey = null
                )
            } else {
                AttachmentKeyData(
                    bucketEncFileSessionKey = null,
                    file = fileId,
                    fileSessionKey = fileSessionKey
                )
            }
        }

        val requestBody = SendDraftData(
            bucketEncMailSessionKey = bucketEncMailSessionKey,
            language = language,
            mailSessionKey = sessionKeyToPass,
            plaintext = false, // for now
            senderNameUnencrypted = null, // for now
            attachmentKeyData = attachmentKeyData,
            internalRecipientKeyData = internalRecipientKeyData,
            mail = draft._id,
            secureExternalRecipientKeyData = listOf() // for now
        )
        api.serviceRequestVoid("tutanota", "senddraftservice", HttpMethod.Post, requestBody)
    }

    suspend fun moveMails(ids: List<IdTuple>, targetFolder: IdTuple) {
        val moveMailData = MoveMailData(
            _format = 0,
            mails = ids,
            targetFolder = targetFolder
        )
        api.serviceRequestVoid(
            "tutanota",
            "movemailservice",
            HttpMethod.Post,
            moveMailData,
            null,
            null
        )
    }

    suspend fun getEnabledMailAddresses(
        user: User,
        userGroupInfo: GroupInfo,
        mailGroup: Group
    ): List<String> {
        return if (mailGroup.user != null) {
            getEnabledMailAddressesForGroupInfo(userGroupInfo)
        } else {
            // TODO: placeholder impl for now
            val mailGroupInfo = api.loadListElementEntity<GroupInfo>(mailGroup.groupInfo)
            getEnabledMailAddressesForGroupInfo(mailGroupInfo)
        }
    }

    suspend fun resolveRecipient(mailAddress: String): RecipientType {
        return try {
            api.serviceRequest(
                "sys", "publickeyservice", HttpMethod.Get, PublicKeyData(mailAddress),
                PublicKeyReturn::class
            )
            RecipientType.INTENRAL
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.NotFound) {
                RecipientType.EXTERNAL
            } else {
                throw e
            }
        }
    }

    private suspend fun getMailGroupIdForMailAddress(user: User, mailAddress: String): Id {
        val mailMemberships = getUserGroupMemberships(user, GroupType.Mail)
        val filteredMemberships = mailMemberships.filter { groupMembership ->
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


    private fun recipientInfoToDraftRecipient(
        toRecipients: List<RecipientInfo>
    ): List<DraftRecipient> {
        return toRecipients.map { ri ->
            DraftRecipient(
                mailAddress = ri.mailAddress,
                name = ri.name
            )
        }
    }

    private suspend fun encryptBucketKeyForInternalRecipient(
        bucketKey: ByteArray,
        recipientAddress: String
    ): InternalRecipientKeyData? {
        val publicKeyData = api.serviceRequest(
            "sys",
            "publickeyservice",
            HttpMethod.Get,
            PublicKeyData(recipientAddress),
            PublicKeyReturn::class
        )
        val publicKey = hexToPublicKey(bytesToHex(publicKeyData.pubKey))
        val encrypted = cryptor.rsaEncrypt(bucketKey, publicKey)
        return InternalRecipientKeyData(
            mailAddress = recipientAddress,
            pubEncBucketKey = encrypted,
            pubKeyVersion = publicKeyData.pubKeyVersion
        )
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
