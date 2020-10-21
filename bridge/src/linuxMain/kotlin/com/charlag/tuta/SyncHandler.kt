package com.charlag.tuta

import UserController
import com.charlag.loadLastEntityEventBatchId
import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.sys.EntityEventBatch
import com.charlag.tuta.entities.sys.EntityUpdate
import com.charlag.tuta.entities.tutanota.*
import com.charlag.tuta.network.API
import com.charlag.tuta.util.timestmpToGeneratedId
import com.charlag.writeLastEntityEventBatchId
import io.ktor.client.features.*
import io.ktor.http.*
import kotlinx.datetime.Clock
import kotlin.reflect.KClass
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class SyncHandler(
    private val api: API,
    private val mailDb: MailDb,
    private val userController: UserController
) {
    suspend fun initialSync() {
        log("Initial sync")

        // Should probably request in reverse till either some number or the date
        val folders = getFolders()
        mailDb.writeFolders(folders)
        log("Wrote folders")

        val dateStart = Clock.System.now().minus(14.toDuration(DurationUnit.DAYS))
        log("Downloading emails since $dateStart")
        val startMs = dateStart.toEpochMilliseconds()
        val startId = timestmpToGeneratedId(startMs, 0)
        // We need to collect all mails from all folders to insert them in correct order so that
        // they get increasing UIDs assigned
        val mails = mutableListOf<Mail>()
        for (folder in folders) {
            val folderMails = api.loadRangeInBetween(Mail::class, folder.mails, startId)
            log("Downloaded mails ${folderMails.size} for ${MailFolderType.fromRaw(folder.folderType)}")
            mails += folderMails
        }
        mails.sortBy { it.getId().elementId.asString() }

        for (mail in mails) {
            try {
                mailDb.insertMail(mail, replace = false)
            } catch (e: DbException) {
                log("Inserting mail $mail failed: $e")
            }
            if (mail.attachments.isNotEmpty()) {
                val listId = mail.attachments.first().listId
                val ids = mail.attachments.map { it.elementId }
                val attachments = api.loadMultipleListElementEntities(File::class, listId, ids)
                for (attachment in attachments) {
                    try {
                        mailDb.writeFile(attachment)
                    } catch (e: DbException) {
                        log("Inserting file $attachment w/ id ${attachment._id} failed")
                    }
                }
            }
        }
        log("Synced ${mails.size} mails")
    }

    suspend fun resync() {
        log("Resync")
        val lastId = loadLastEntityEventBatchId()?.let { GeneratedId(it) }
        val mailMembership =
            userController.user!!.memberships.first { it.groupType == GroupType.Mail.value }
        if (lastId == null) {
            log("No last batch id")
            val batch = api.loadRange(
                EntityEventBatch::class, mailMembership.group,
                GENERATED_MAX_ID,
                1,
                newToOld = true,
            ).firstOrNull() ?: run {
                log("No batches")
                return
            }
            val newLastBatch = batch._id!!.elementId.asString()
            writeLastEntityEventBatchId(newLastBatch)
            log("New last batch is $newLastBatch")
        } else {
            val batches =
                api.loadRangeInBetween(
                    EntityEventBatch::class,
                    mailMembership.group,
                    lastId
                )

            for (batch in batches) {
                for (entityUpdate in batch.events) {
                    val typeInfo = typemodelMap[entityUpdate.type] ?: continue
                    log("Update: ${typeInfo.typemodel.name} ${entityUpdate.operation}")
                    when (entityUpdate.operation) {
                        EntityOperation.CREATE,
                        EntityOperation.UPDATE -> downloadAndInsert(typeInfo, entityUpdate)
                        EntityOperation.DELETE -> delete(typeInfo, entityUpdate)
                    }
                }
            }
            batches.lastOrNull()?.let {
                val newLastBatch = it._id!!.elementId.asString()
                log("New last batch $newLastBatch")
                writeLastEntityEventBatchId(newLastBatch)
            }
        }
        log("Resync done")
    }

    private suspend fun <T : Entity> downloadAndInsert(
        typeInfo: TypeInfo<T>,
        entityUpdate: EntityUpdate
    ) {
        when (typeInfo) {
            MailTypeInfo -> {
                val instance: Mail =
                    downloadBatchInstance(api, MailTypeInfo, entityUpdate)
                        ?: return
                mailDb.insertMail(instance)
            }
            MailFolderTypeInfo -> {
                val instance =
                    downloadBatchInstance(api, MailFolderTypeInfo, entityUpdate)
                        ?: return
                mailDb.writeFolder(instance)
            }
            FileTypeInfo -> {
                val instance = downloadBatchInstance(api, FileTypeInfo, entityUpdate) ?: return
                mailDb.writeFile(instance)
            }
        }
    }

    private suspend fun <T : Entity> downloadBatchInstance(
        api: API, typeInfo: TypeInfo<T>,
        entityUpdate: EntityUpdate
    ): T? {
        return try {
            if (typeInfo.typemodel.type == MetamodelType.LIST_ELEMENT_TYPE) {
                @Suppress("UNCHECKED_CAST")
                api.loadListElementEntity(
                    typeInfo.klass as KClass<out ListElementEntity>,
                    IdTuple(
                        GeneratedId(entityUpdate.instanceListId),
                        GeneratedId(entityUpdate.instanceId)
                    )
                ) as T
            } else {
                api.loadElementEntity(typeInfo.klass, GeneratedId(entityUpdate.instanceId))
            }
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.NotFound) {
                // pass
                null
            } else {
                throw e
            }
        }
    }

    private fun <T : Entity> delete(typeInfo: TypeInfo<T>, entityUpdate: EntityUpdate) {
        when (typeInfo) {
            MailTypeInfo -> mailDb.deleteMail(
                IdTuple(
                    GeneratedId(entityUpdate.instanceListId),
                    GeneratedId(entityUpdate.instanceId)
                )
            )
            MailFolderTypeInfo -> mailDb.deleteFolder(
                IdTuple(
                    GeneratedId(entityUpdate.instanceListId),
                    GeneratedId(entityUpdate.instanceId)
                )
            )
            MailBodyTypeInfo -> mailDb.deleteBody(entityUpdate.instanceId)
            FileTypeInfo -> mailDb.deleteFile(
                IdTuple(
                    GeneratedId(entityUpdate.instanceListId),
                    GeneratedId(entityUpdate.instanceId)
                )
            )
        }
    }

    private suspend fun getFolders(): List<MailFolder> {
        val user = userController.user ?: error("Not logged in!")
        val mailMembership = user.memberships.first { it.groupType == GroupType.Mail.value }

        val groupRoot = api
            .loadElementEntity<MailboxGroupRoot>(mailMembership.group)
        val mailbox = api.loadElementEntity<MailBox>(groupRoot.mailbox)
        return api.loadAll(MailFolder::class, mailbox.systemFolders!!.folders)
    }

    private fun log(any: Any) {
        print("SyncHandler: ")
        println(any)
    }
}