@file:UseSerializers(
    ByteArraySerializer::class,
    DateSerializer::class
)

package com.charlag.tuta.entities.tutanota


import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.sys.IdTuple
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class DataBlock(
    val _id: Id,
    val blockData: Id,
    val size: Long
) : Entity

@Serializable
data class FileData(
    val _format: Long,
    val _id: Id,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val size: Long,
    val unreferenced: Boolean,
    val blocks: List<DataBlock>
) : Entity

@Serializable
data class Subfiles(
    val _id: Id,
    val files: Id
) : Entity

@Serializable
data class TutanotaFile(
    val _area: Long,
    val _format: Long,
    val _id: IdTuple,
    val _owner: Id,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val cid: String?,
    val mimeType: String?,
    val name: String,
    val size: Long,
    val subFiles: Subfiles?,
    val data: Id?,
    val parent: IdTuple?
) : Entity

@Serializable
data class FileSystem(
    val _format: Long,
    val _id: Id,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val files: Id
) : Entity

@Serializable
data class MailBody(
    val _area: Long,
    val _format: Long,
    val _id: Id,
    val _owner: Id,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val compressedText: String?,
    val text: String?
) : Entity

@Serializable
data class ContactMailAddress(
    val _id: Id,
    val address: String,
    val customTypeName: String,
    val type: Long
) : Entity

@Serializable
data class ContactPhoneNumber(
    val _id: Id,
    val customTypeName: String,
    val number: String,
    val type: Long
) : Entity

@Serializable
data class ContactAddress(
    val _id: Id,
    val address: String,
    val customTypeName: String,
    val type: Long
) : Entity

@Serializable
data class ContactSocialId(
    val _id: Id,
    val customTypeName: String,
    val socialId: String,
    val type: Long
) : Entity

@Serializable
data class Contact(
    val _area: Long,
    val _format: Long,
    val _id: IdTuple,
    val _owner: Id,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val autoTransmitPassword: String,
    val comment: String,
    val company: String,
    val firstName: String,
    val lastName: String,
    val nickname: String?,
    val oldBirthday: Date?,
    val presharedPassword: String?,
    val role: String,
    val title: String?,
    val addresses: List<ContactAddress>,
    val birthday: Birthday?,
    val mailAddresses: List<ContactMailAddress>,
    val phoneNumbers: List<ContactPhoneNumber>,
    val socialIds: List<ContactSocialId>,
    val photo: IdTuple?
) : Entity

@Serializable
data class ConversationEntry(
    val _format: Long,
    val _id: IdTuple,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val conversationType: Long,
    val messageId: String,
    val mail: IdTuple?,
    val previous: IdTuple?
) : Entity

@Serializable
data class MailAddress(
    val _id: Id,
    val address: String,
    val name: String,
    val contact: IdTuple?
) : Entity

@Serializable
data class Mail(
    val _area: Long,
    val _format: Long,
    val _id: IdTuple,
    val _owner: Id,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
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
    val bccRecipients: List<MailAddress>,
    val ccRecipients: List<MailAddress>,
    val replyTos: List<EncryptedMailAddress>,
    val restrictions: MailRestriction?,
    val sender: MailAddress,
    val toRecipients: List<MailAddress>,
    val attachments: List<IdTuple>,
    val body: Id,
    val conversationEntry: IdTuple,
    val headers: Id?
) : Entity

@Serializable
data class MailBox(
    val _format: Long,
    val _id: Id,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val lastInfoDate: Date,
    val symEncShareBucketKey: ByteArray?,
    val systemFolders: MailFolderRef?,
    val mails: Id,
    val receivedAttachments: Id,
    val sentAttachments: Id
) : Entity

@Serializable
data class PasswordChannelPhoneNumber(
    val _id: Id,
    val number: String
) : Entity

@Serializable
data class CreateExternalUserGroupData(
    val _id: Id,
    val internalUserEncUserGroupKey: ByteArray,
    val mailAddress: String,
    val externalPwEncUserGroupKey: ByteArray
) : Entity

@Serializable
data class ExternalUserData(
    val _format: Long,
    val externalMailEncMailBoxSessionKey: ByteArray,
    val externalMailEncMailGroupInfoSessionKey: ByteArray,
    val externalUserEncEntropy: ByteArray,
    val externalUserEncMailGroupKey: ByteArray,
    val externalUserEncTutanotaPropertiesSessionKey: ByteArray,
    val externalUserEncUserGroupInfoSessionKey: ByteArray,
    val internalMailEncMailGroupInfoSessionKey: ByteArray,
    val internalMailEncUserGroupInfoSessionKey: ByteArray,
    val userEncClientKey: ByteArray,
    val verifier: ByteArray,
    val userGroupData: CreateExternalUserGroupData
) : Entity

@Serializable
data class ContactList(
    val _format: Long,
    val _id: Id,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val photos: PhotosRef?,
    val contacts: Id
) : Entity

@Serializable
data class RemoteImapSyncInfo(
    val _format: Long,
    val _id: IdTuple,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val seen: Boolean,
    val message: IdTuple
) : Entity

@Serializable
data class ImapFolder(
    val _id: Id,
    val lastseenuid: String,
    val name: String,
    val uidvalidity: String,
    val syncInfo: Id
) : Entity

@Serializable
data class ImapSyncState(
    val _format: Long,
    val _id: Id,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val folders: List<ImapFolder>
) : Entity

@Serializable
data class ImapSyncConfiguration(
    val _id: Id,
    val host: String,
    val password: String,
    val port: Long,
    val user: String,
    val imapSyncState: Id?
) : Entity

@Serializable
data class TutanotaProperties(
    val _format: Long,
    val _id: Id,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val customEmailSignature: String,
    val defaultSender: String?,
    val defaultUnconfidential: Boolean,
    val emailSignatureType: Long,
    val groupEncEntropy: ByteArray?,
    val lastSeenAnnouncement: Long,
    val noAutomaticContacts: Boolean,
    val notificationMailLanguage: String?,
    val sendPlaintextOnly: Boolean,
    val imapSyncConfig: List<ImapSyncConfiguration>,
    val inboxRules: List<InboxRule>,
    val lastPushedMail: IdTuple?
) : Entity

@Serializable
data class NotificationMail(
    val _id: Id,
    val bodyText: String,
    val mailboxLink: String,
    val recipientMailAddress: String,
    val recipientName: String,
    val subject: String
) : Entity

@Serializable
data class PasswordMessagingData(
    val _format: Long,
    val language: String,
    val numberId: Id,
    val symKeyForPasswordTransmission: ByteArray
) : Entity

@Serializable
data class PasswordMessagingReturn(
    val _format: Long,
    val autoAuthenticationId: Id
) : Entity

@Serializable
data class PasswordAutoAuthenticationReturn(
    val _format: Long
) : Entity

@Serializable
data class PasswordRetrievalData(
    val _format: Long,
    val autoAuthenticationId: Id
) : Entity

@Serializable
data class PasswordRetrievalReturn(
    val _format: Long,
    val transmissionKeyEncryptedPassword: String
) : Entity

@Serializable
data class PasswordChannelReturn(
    val _format: Long,
    val phoneNumberChannels: List<PasswordChannelPhoneNumber>
) : Entity

@Serializable
data class FileDataDataGet(
    val _format: Long,
    val base64: Boolean,
    val file: IdTuple
) : Entity

@Serializable
data class FileDataDataPost(
    val _format: Long,
    val group: Id,
    val size: Long
) : Entity

@Serializable
data class FileDataDataReturn(
    val _format: Long,
    val size: Long
) : Entity

@Serializable
data class FileDataReturnPost(
    val _format: Long,
    val fileData: Id
) : Entity

@Serializable
data class CreateFileData(
    val _format: Long,
    val fileName: String,
    val group: Id,
    val ownerEncSessionKey: ByteArray,
    val mimeType: String,
    val fileData: Id,
    val parentFolder: IdTuple?
) : Entity

@Serializable
data class DeleteMailData(
    val _format: Long,
    val folder: IdTuple?,
    val mails: List<IdTuple>
) : Entity

@Serializable
data class MailFolder(
    val _format: Long,
    val _id: IdTuple,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val folderType: Long,
    val name: String,
    val mails: Id,
    val parentFolder: IdTuple?,
    val subFolders: Id
) : Entity

@Serializable
data class MailFolderRef(
    val _id: Id,
    val folders: Id
) : Entity

@Serializable
data class MoveMailData(
    val _format: Long,
    val mails: List<IdTuple>,
    val targetFolder: IdTuple
) : Entity

@Serializable
data class CreateMailFolderData(
    val _format: Long,
    val folderName: String,
    val ownerEncSessionKey: ByteArray,
    val parentFolder: IdTuple
) : Entity

@Serializable
data class CreateMailFolderReturn(
    val _format: Long,
    val newFolder: IdTuple
) : Entity

@Serializable
data class DeleteMailFolderData(
    val _format: Long,
    val folders: List<IdTuple>
) : Entity

@Serializable
data class EncryptTutanotaPropertiesData(
    val _format: Long,
    val symEncSessionKey: ByteArray,
    val properties: Id
) : Entity

@Serializable
data class DraftRecipient(
    val _id: Id,
    val mailAddress: String,
    val name: String
) : Entity

@Serializable
data class NewDraftAttachment(
    val _id: Id,
    val encCid: ByteArray?,
    val encFileName: ByteArray,
    val encMimeType: ByteArray,
    val fileData: Id
) : Entity

@Serializable
data class DraftAttachment(
    val _id: Id,
    val ownerEncFileSessionKey: ByteArray,
    val newFile: NewDraftAttachment?,
    val existingFile: IdTuple?
) : Entity

@Serializable
data class DraftData(
    val _id: Id,
    val bodyText: String,
    val confidential: Boolean,
    val senderMailAddress: String,
    val senderName: String,
    val subject: String,
    val addedAttachments: List<DraftAttachment>,
    val bccRecipients: List<DraftRecipient>,
    val ccRecipients: List<DraftRecipient>,
    val replyTos: List<EncryptedMailAddress>,
    val toRecipients: List<DraftRecipient>,
    val removedAttachments: List<IdTuple>
) : Entity

@Serializable
data class DraftCreateData(
    val _format: Long,
    val conversationType: Long,
    val ownerEncSessionKey: ByteArray,
    val previousMessageId: String?,
    val symEncSessionKey: ByteArray,
    val draftData: DraftData
) : Entity

@Serializable
data class DraftCreateReturn(
    val _format: Long,
    val draft: IdTuple
) : Entity

@Serializable
data class DraftUpdateData(
    val _format: Long,
    val draftData: DraftData,
    val draft: IdTuple
) : Entity

@Serializable
data class DraftUpdateReturn(
    val _format: Long,
    val attachments: List<IdTuple>
) : Entity

@Serializable
data class InternalRecipientKeyData(
    val _id: Id,
    val mailAddress: String,
    val pubEncBucketKey: ByteArray,
    val pubKeyVersion: Long
) : Entity

@Serializable
data class SecureExternalRecipientKeyData(
    val _id: Id,
    val autoTransmitPassword: String?,
    val mailAddress: String,
    val ownerEncBucketKey: ByteArray?,
    val passwordVerifier: ByteArray,
    val pwEncCommunicationKey: ByteArray?,
    val salt: ByteArray?,
    val saltHash: ByteArray?,
    val symEncBucketKey: ByteArray?,
    val passwordChannelPhoneNumbers: List<PasswordChannelPhoneNumber>
) : Entity

@Serializable
data class AttachmentKeyData(
    val _id: Id,
    val bucketEncFileSessionKey: ByteArray?,
    val fileSessionKey: ByteArray?,
    val file: IdTuple
) : Entity

@Serializable
data class SendDraftData(
    val _format: Long,
    val bucketEncMailSessionKey: ByteArray?,
    val language: String,
    val mailSessionKey: ByteArray?,
    val plaintext: Boolean,
    val senderNameUnencrypted: String?,
    val attachmentKeyData: List<AttachmentKeyData>,
    val internalRecipientKeyData: List<InternalRecipientKeyData>,
    val secureExternalRecipientKeyData: List<SecureExternalRecipientKeyData>,
    val mail: IdTuple
) : Entity

@Serializable
data class SendDraftReturn(
    val _format: Long,
    val messageId: String,
    val sentDate: Date,
    val notifications: List<NotificationMail>,
    val sentMail: IdTuple
) : Entity

@Serializable
data class ReceiveInfoServiceData(
    val _format: Long
) : Entity

@Serializable
data class InboxRule(
    val _id: Id,
    val type: String,
    val value: String,
    val targetFolder: IdTuple
) : Entity

@Serializable
data class MailHeaders(
    val _format: Long,
    val _id: Id,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val compressedHeaders: String?,
    val headers: String?
) : Entity

@Serializable
data class EncryptedMailAddress(
    val _id: Id,
    val address: String,
    val name: String
) : Entity

@Serializable
data class UserAccountUserData(
    val _id: Id,
    val contactEncContactListSessionKey: ByteArray,
    val customerEncContactGroupInfoSessionKey: ByteArray,
    val customerEncFileGroupInfoSessionKey: ByteArray,
    val customerEncMailGroupInfoSessionKey: ByteArray,
    val encryptedName: ByteArray,
    val fileEncFileSystemSessionKey: ByteArray,
    val mailAddress: String,
    val mailEncMailBoxSessionKey: ByteArray,
    val pwEncUserGroupKey: ByteArray,
    val recoverCodeEncUserGroupKey: ByteArray,
    val recoverCodeVerifier: ByteArray,
    val salt: ByteArray,
    val userEncClientKey: ByteArray,
    val userEncContactGroupKey: ByteArray,
    val userEncCustomerGroupKey: ByteArray,
    val userEncEntropy: ByteArray,
    val userEncFileGroupKey: ByteArray,
    val userEncMailGroupKey: ByteArray,
    val userEncRecoverCode: ByteArray,
    val userEncTutanotaPropertiesSessionKey: ByteArray,
    val verifier: ByteArray
) : Entity

@Serializable
data class InternalGroupData(
    val _id: Id,
    val adminEncGroupKey: ByteArray,
    val groupEncPrivateKey: ByteArray,
    val ownerEncGroupInfoSessionKey: ByteArray,
    val publicKey: ByteArray,
    val adminGroup: Id?
) : Entity

@Serializable
data class CustomerAccountCreateData(
    val _format: Long,
    val adminEncAccountingInfoSessionKey: ByteArray,
    val adminEncCustomerServerPropertiesSessionKey: ByteArray,
    val authToken: String,
    val code: String,
    val date: Date?,
    val lang: String,
    val systemAdminPubEncAccountingInfoSessionKey: ByteArray,
    val userEncAccountGroupKey: ByteArray,
    val userEncAdminGroupKey: ByteArray,
    val adminGroupData: InternalGroupData,
    val customerGroupData: InternalGroupData,
    val userData: UserAccountUserData,
    val userGroupData: InternalGroupData
) : Entity

@Serializable
data class UserAccountCreateData(
    val _format: Long,
    val date: Date?,
    val userData: UserAccountUserData,
    val userGroupData: InternalGroupData
) : Entity

@Serializable
data class MailboxServerProperties(
    val _format: Long,
    val _id: Id,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val whitelistProtectionEnabled: Boolean
) : Entity

@Serializable
data class MailboxGroupRoot(
    val _format: Long,
    val _id: Id,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val contactFormUserContactForm: IdTuple?,
    val mailbox: Id,
    val participatingContactForms: List<IdTuple>,
    val serverProperties: Id,
    val targetMailGroupContactForm: IdTuple?,
    val whitelistRequests: Id
) : Entity

@Serializable
data class CreateLocalAdminGroupData(
    val _format: Long,
    val encryptedName: ByteArray,
    val groupData: InternalGroupData
) : Entity

@Serializable
data class CreateMailGroupData(
    val _format: Long,
    val encryptedName: ByteArray,
    val mailAddress: String,
    val mailEncMailboxSessionKey: ByteArray,
    val groupData: InternalGroupData
) : Entity

@Serializable
data class DeleteGroupData(
    val _format: Long,
    val restore: Boolean,
    val group: Id
) : Entity

@Serializable
data class MailRestriction(
    val _id: Id,
    val delegationGroups_removed: List<Id>,
    val participantGroupInfos: List<IdTuple>
) : Entity

@Serializable
data class Name(
    val _id: Id,
    val name: String
) : Entity

@Serializable
data class InputField(
    val _id: Id,
    val name: String,
    val type: Long,
    val enumValues: List<Name>
) : Entity

@Serializable
data class ContactForm(
    val _format: Long,
    val _id: IdTuple,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val path: String,
    val languages: List<ContactFormLanguage>,
    val statisticsFields_removed: List<InputField>,
    val statisticsLog: StatisticLogRef?,
    val delegationGroups_removed: List<Id>,
    val participantGroupInfos: List<IdTuple>,
    val targetGroup: Id,
    val targetGroupInfo: IdTuple?
) : Entity

@Serializable
data class ContactFormAccountReturn(
    val _format: Long,
    val requestMailAddress: String,
    val responseMailAddress: String
) : Entity

@Serializable
data class ContactFormUserData(
    val _id: Id,
    val mailEncMailBoxSessionKey: ByteArray,
    val ownerEncMailGroupInfoSessionKey: ByteArray,
    val pwEncUserGroupKey: ByteArray,
    val salt: ByteArray,
    val userEncClientKey: ByteArray,
    val userEncEntropy: ByteArray,
    val userEncMailGroupKey: ByteArray,
    val userEncTutanotaPropertiesSessionKey: ByteArray,
    val verifier: ByteArray
) : Entity

@Serializable
data class ContactFormStatisticField(
    val _id: Id,
    val encryptedName: ByteArray,
    val encryptedValue: ByteArray
) : Entity

@Serializable
data class ContactFormEncryptedStatisticsField(
    val _id: Id,
    val name: String,
    val value: String
) : Entity

@Serializable
data class StatisticLogEntry(
    val _format: Long,
    val _id: IdTuple,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val date: Date,
    val values: List<ContactFormEncryptedStatisticsField>,
    val contactForm: IdTuple
) : Entity

@Serializable
data class CustomerContactFormGroupRoot(
    val _format: Long,
    val _id: Id,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val contactFormConversations: DeleteContactFormConversationIndex?,
    val statisticsLog: UnencryptedStatisticLogRef?,
    val contactForms: Id,
    val statisticsLog_encrypted_removed: Id
) : Entity

@Serializable
data class ContactFormAccountData(
    val _format: Long,
    val statisticFields: List<ContactFormStatisticField>,
    val statistics: ContactFormStatisticEntry?,
    val userData: ContactFormUserData,
    val userGroupData: InternalGroupData,
    val contactForm: IdTuple
) : Entity

@Serializable
data class ContactFormStatisticEntry(
    val _id: Id,
    val bucketEncSessionKey: ByteArray,
    val customerPubEncBucketKey: ByteArray,
    val customerPubKeyVersion: Long,
    val statisticFields: List<ContactFormStatisticField>
) : Entity

@Serializable
data class DeleteContactFormConversationIndexEntry(
    val _format: Long,
    val _id: IdTuple,
    val _ownerGroup: Id?,
    val _permissions: Id
) : Entity

@Serializable
data class DeleteContactFormConversationIndex(
    val _id: Id,
    val items: Id
) : Entity

@Serializable
data class Birthday(
    val _id: Id,
    val day: Long,
    val month: Long,
    val year: Long?
) : Entity

@Serializable
data class PhotosRef(
    val _id: Id,
    val files: Id
) : Entity

@Serializable
data class ContactFormLanguage(
    val _id: Id,
    val code: String,
    val footerHtml: String,
    val headerHtml: String,
    val helpHtml: String,
    val pageTitle: String,
    val statisticsFields: List<InputField>
) : Entity

@Serializable
data class ListUnsubscribeData(
    val _format: Long,
    val headers: String,
    val recipient: String,
    val mail: IdTuple
) : Entity

@Serializable
data class StatisticLogRef(
    val _id: Id,
    val items: Id
) : Entity

@Serializable
data class UnencryptedStatisticLogEntry(
    val _format: Long,
    val _id: IdTuple,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val contactFormPath: String,
    val date: Date
) : Entity

@Serializable
data class UnencryptedStatisticLogRef(
    val _id: Id,
    val items: Id
) : Entity

@Serializable
data class CalendarRepeatRule(
    val _id: Id,
    val endType: Long,
    val endValue: Long?,
    val frequency: Long,
    val interval: Long,
    val timeZone: String
) : Entity

@Serializable
data class CalendarEvent(
    val _format: Long,
    val _id: IdTuple,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val description: String,
    val endTime: Date,
    val location: String,
    val startTime: Date,
    val summary: String,
    val uid: String?,
    val repeatRule: CalendarRepeatRule?,
    val alarmInfos: List<IdTuple>
) : Entity

@Serializable
data class CalendarGroupRoot(
    val _format: Long,
    val _id: Id,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val longEvents: Id,
    val shortEvents: Id
) : Entity

@Serializable
data class CalendarGroupData(
    val _id: Id,
    val adminEncGroupKey: ByteArray?,
    val calendarEncCalendarGroupRootSessionKey: ByteArray,
    val groupInfoEncName: ByteArray,
    val ownerEncGroupInfoSessionKey: ByteArray,
    val userEncGroupKey: ByteArray,
    val adminGroup: Id?
) : Entity

@Serializable
data class CalendarPostData(
    val _format: Long,
    val calendarData: CalendarGroupData
) : Entity

@Serializable
data class GroupColor(
    val _id: Id,
    val color: String,
    val group: Id
) : Entity

@Serializable
data class UserSettingsGroupRoot(
    val _format: Long,
    val _id: Id,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val startOfTheWeek: Long,
    val timeFormat: Long,
    val groupColors: List<GroupColor>
) : Entity

@Serializable
data class CalendarDeleteData(
    val _format: Long,
    val groupRootId: Id
) : Entity

@Serializable
data class CalendarPostReturn(
    val _format: Long,
    val group: Id
) : Entity
