package com.charlag.tuta.data

import androidx.room.TypeConverter
import com.charlag.tuta.ConversationType
import com.charlag.tuta.RecipientType
import com.charlag.tuta.base64ToBytes
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.IdTuple
import com.charlag.tuta.entities.tutanota.*
import com.charlag.tuta.toBase64
import kotlinx.serialization.internal.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.serializer
import java.util.*
import kotlin.collections.HashMap

val json = Json(JsonConfiguration.Stable)

class TutanotaConverters {

    @TypeConverter
    fun mailAddressToString(mailAddress: MailAddressEntity): String {
        return json.stringify(MailAddressEntity.serializer(), mailAddress)
    }

    @TypeConverter
    fun stringToMailAddress(string: String): MailAddressEntity {
        return json.parse(MailAddressEntity.serializer(), string)
    }

    val mailAddressListSerializer = ArrayListSerializer(MailAddressEntity.serializer())

    @TypeConverter
    fun mailAddressListToString(mailAddresses: List<MailAddressEntity>): String {
        return json.stringify(mailAddressListSerializer, mailAddresses)
    }

    @TypeConverter
    fun stringToMailAddressess(string: String): List<MailAddressEntity> {
        return json.parse(mailAddressListSerializer, string)
    }

    @TypeConverter
    fun idToString(id: Id?): String? = id?.asString()

    @TypeConverter
    fun stringToId(string: String?): Id? = string?.let(::GeneratedId)

    // Doing IdTuple manually as automatic conversion fails somehow
    @TypeConverter
    fun idTupleToString(idTuple: IdTuple?): String? {
        idTuple ?: return null
        return "[\"${idTuple.listId.asString()}\", \"${idTuple.elementId.asString()}\"]"
    }

    @TypeConverter
    fun stringToIdTuple(string: String?): IdTuple? {
        string ?: return null
        val array = json.parseJson(string).jsonArray
        return IdTuple(
            GeneratedId(array[0].primitive.content),
            GeneratedId(array[1].primitive.content)
        )
    }

    @TypeConverter
    fun idTupleListToString(idTuples: List<IdTuple>): String {
        return idTuples.joinToString(
            prefix = "[",
            separator = ",",
            postfix = "]"
        ) { this.idTupleToString(it)!! }
    }

    @TypeConverter
    fun stringToIdTupleList(string: String): List<IdTuple> {
        return json.parseJson(string).jsonArray
            .map { jsontuple ->
                IdTuple(
                    GeneratedId(jsontuple.jsonArray[0].primitive.content),
                    GeneratedId(jsontuple.jsonArray[1].primitive.content)
                )
            }
    }

    private val ivsSerializer = HashMapSerializer(String.serializer(), String.serializer().nullable)

    @TypeConverter
    fun finalIvsToString(ivs: Map<String, ByteArray?>?): String? {
        ivs ?: return null
        return ivs.mapValuesTo(HashMap()) { (k, v) -> v?.toBase64() }
            .let { json.stringify(ivsSerializer, it) }
    }

    @TypeConverter
    fun stringToFinalIvs(string: String?): Map<String, ByteArray?>? {
        string ?: return null
        return json.parse(ivsSerializer, string).mapValues { (_, v) -> v?.let(::base64ToBytes) }
    }

    @TypeConverter
    fun recipientTypeToInt(recipientType: RecipientType): Int {
        return recipientType.raw
    }

    @TypeConverter
    fun intToRecipientType(raw: Int): RecipientType {
        return RecipientType.fromRaw(raw)
    }

    @TypeConverter
    fun conversationTypeToLong(conversationType: ConversationType): Long {
        return conversationType.value
    }

    @TypeConverter
    fun longToConversationType(raw: Long): ConversationType {
        return ConversationType.fromRaw(raw)
    }
}

class DateConverter {
    @TypeConverter
    fun dateToLong(date: Date?): Long = date?.time ?: -1

    @TypeConverter
    fun longToDate(long: Long): Date? = if (long == -1L) null else Date(long)
}

fun MailAddress.toEntity() = MailAddressEntity(_id?.asString(), name, address, contact, finalIvs)
fun Mail.toEntity() = MailEntity(
    id = _id!!.elementId.asString(),
    listId = _id!!.listId.asString(),
    _owner = _owner!!,
    _ownerGroup = _ownerGroup,
    _permissions = _permissions!!,
    _ownerEncSessionKey = _ownerEncSessionKey,
    confidential = confidential,
    differentEnvelopeSender = differentEnvelopeSender,
    listUnsubscribe = listUnsubscribe,
    movedTime = movedTime?.toDate(),
    receivedDate = receivedDate.toDate(),
    replyType = replyType,
    sentDate = sentDate.toDate(),
    state = state,
    subject = subject,
    trashed = trashed,
    unread = unread,
    sender = sender.toEntity(),
    toRecipients = toRecipients.map { it.toEntity() },
    ccRecipients = ccRecipients.map { it.toEntity() },
    bccRecipients = bccRecipients.map { it.toEntity() },
    replyTos = replyTos.map { it.toEntity() },
    body = body,
    conversationEntry = conversationEntry,
    attachments = attachments,
    headers = headers,
    finalIvs = finalIvs
)

fun MailEntity.toMail() = Mail(
    _id = IdTuple(
        GeneratedId(listId),
        GeneratedId(id)
    ),
    _owner = _owner,
    _ownerGroup = _ownerGroup,
    _permissions = _permissions,
    _ownerEncSessionKey = _ownerEncSessionKey,
    confidential = confidential,
    differentEnvelopeSender = differentEnvelopeSender,
    listUnsubscribe = listUnsubscribe,
    movedTime = movedTime?.toDate(),
    receivedDate = receivedDate.toDate(),
    replyType = replyType,
    sentDate = sentDate.toDate(),
    state = state,
    subject = subject,
    trashed = trashed,
    unread = unread,
    sender = sender.toMailAddress(),
    toRecipients = toRecipients.map { it.toMailAddress() },
    ccRecipients = ccRecipients.map { it.toMailAddress() },
    bccRecipients = bccRecipients.map { it.toMailAddress() },
    replyTos = replyTos.map { it.toEncryptedMailAddress() },
    body = body,
    conversationEntry = conversationEntry,
    attachments = attachments,
    headers = headers,
    restrictions = null,
    authStatus = 0,
    phishingStatus = 0
).also {
    it.finalIvs = finalIvs
}

fun MailFolder.toEntity() = MailFolderEntity(
    id = _id!!.elementId.asString(),
    listId = _id!!.listId.asString(),
    folderType = folderType,
    name = name,
    mails = mails,
    ownerEncSessionKey = _ownerEncSessionKey,
    ownerGroup = _ownerGroup,
    permissions = _permissions!!,
    parentFolder = parentFolder,
    subFolders = subFolders
)

fun MailFolderEntity.toFolder() = MailFolder(
    _format = 0,
    _id = IdTuple.fromRawValues(listId, id),
    _ownerEncSessionKey = ownerEncSessionKey,
    _ownerGroup = ownerGroup,
    _permissions = permissions,
    folderType = folderType,
    name = name,
    parentFolder = parentFolder,
    mails = mails,
    subFolders = subFolders

)

fun EncryptedMailAddress.toEntity() =
    MailAddressEntity(_id?.asString(), name, address, null, finalIvs)

fun com.charlag.tuta.entities.Date.toDate() = Date(millis)

fun Date.toDate() = com.charlag.tuta.entities.Date(time)

fun MailAddressEntity.toMailAddress() = MailAddress(id?.let(::GeneratedId), address, name, null)
    .also {
        it.finalIvs = finalIvs
    }

fun MailAddressEntity.toEncryptedMailAddress() =
    EncryptedMailAddress(id?.let(::GeneratedId), address, name).also {
        it.finalIvs = finalIvs
    }


fun Contact.toEntity() = ContactEntity(
    id = _id!!.elementId.asString(),
    listId = _id!!.listId.asString(),
    _owner = _owner!!,
    _ownerEncSessionKey = _ownerEncSessionKey,
    _ownerGroup = _ownerGroup,
    _permissions = _permissions!!,
    autoTransmitPassword = autoTransmitPassword,
    comment = comment,
    company = company,
    firstName = firstName,
    lastName = lastName,
    nickname = nickname,
    oldBirthday = null,
    presharedPassword = presharedPassword,
    role = role,
    title = title,
    addresses = addresses,
    birthday = null, // TODO
    mailAddresses = mailAddresses,
    phoneNumbers = phoneNumbers,
    socialIds = socialIds,
    photo = photo
)

fun ContactEntity.toContact() = Contact(
    _id = IdTuple(
        GeneratedId(listId),
        GeneratedId(id)
    ),
    _owner = _owner,
    _ownerEncSessionKey = _ownerEncSessionKey,
    _ownerGroup = _ownerGroup,
    _permissions = _permissions,
    autoTransmitPassword = autoTransmitPassword,
    comment = comment,
    company = company,
    firstName = firstName,
    lastName = lastName,
    nickname = nickname,
    presharedPassword = presharedPassword,
    role = role,
    title = title,
    addresses = addresses,
    // TODO
    birthdayIso = null,
    mailAddresses = mailAddresses,
    phoneNumbers = phoneNumbers,
    socialIds = socialIds,
    photo = photo,
    oldBirthdayAggregate = null,
    oldBirthdayDate = null
)