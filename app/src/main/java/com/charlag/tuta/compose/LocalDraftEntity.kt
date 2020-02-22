package com.charlag.tuta.compose

import androidx.room.*
import com.charlag.tuta.ConversationType
import com.charlag.tuta.RecipientInfo
import com.charlag.tuta.data.json
import com.charlag.tuta.entities.IdTuple
import kotlinx.serialization.internal.ArrayListSerializer

@Entity
@TypeConverters(LocalDraftConverters::class)
data class LocalDraftEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val user: String,
    val subject: String,
    val body: String,
    val senderAddress: String,
    val senderName: String,
    val toRecipients: List<RecipientInfo>,
    val ccRecipients: List<RecipientInfo>,
    val bccRecipients: List<RecipientInfo>,
    val conversationType: ConversationType,
    val previousMessageId: String?,
    val confidential: Boolean,
    val replyTos: List<RecipientInfo>,
    val files: List<FileReference>,
    val remoteFiles: List<IdTuple>,
    @Embedded(prefix = "prev_mail")
    val previousMail: IdTuple?,
    val replyContent: String?,
    val loadExternalContent: Boolean,
    val remoteDraftId: IdTuple?
)

class LocalDraftConverters {
    val recipientListSerializer = ArrayListSerializer(RecipientInfo.serializer())

    @TypeConverter
    fun recipientinfosToJson(infos: List<RecipientInfo>): String =
        json.stringify(recipientListSerializer, infos)

    @TypeConverter
    fun jsonToRecipientInfos(jsonString: String): List<RecipientInfo> =
        json.parse(recipientListSerializer, jsonString)

    val fileReferenceListSerializer = ArrayListSerializer(FileReference.serializer())

    @TypeConverter
    fun fileRefsToJson(infos: List<FileReference>): String =
        json.stringify(fileReferenceListSerializer, infos)

    @TypeConverter
    fun jsonToFileRefs(jsonString: String): List<FileReference> =
        json.parse(fileReferenceListSerializer, jsonString)
}