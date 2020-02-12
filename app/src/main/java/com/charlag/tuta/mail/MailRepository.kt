package com.charlag.tuta.mail

import com.charlag.tuta.MailFacade
import com.charlag.tuta.compose.LocalDraftEntity
import com.charlag.tuta.data.*
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.network.API
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.entities.tutanota.DeleteMailData
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailBody
import com.charlag.tuta.entities.tutanota.MoveMailData
import io.ktor.http.HttpMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MailRepository(
    private val api: API,
    private val db: AppDatabase,
    private val mailFacade: MailFacade
) {
    // Should this be on the mailFacade?
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

    suspend fun getMail(id: IdTuple): MailEntity {
        return withContext(Dispatchers.Default) {
            db.mailDao().getMail(id.elementId.asString())
                ?: api.loadListElementEntity<Mail>(id).let {
                    val entity = it.toEntity()
                    db.mailDao().insertMail(entity)
                    entity
                }
        }
    }

    suspend fun getMailBody(id: Id): MailBodyEntity {
        return db.mailDao().getMailBody(id.asString()) ?: loadMailBodyFromTheServer(id)
    }

    suspend fun getLocalDraft(id: Long): LocalDraftEntity? {
        return db.mailDao().getLocalDraft(id)
    }

    suspend fun saveLocalDraft(draft: LocalDraftEntity): Long {
        return db.mailDao().insertLocalDraft(draft)
    }

    suspend fun deleteLocalDraft(draft: LocalDraftEntity) {
        db.mailDao().deleteLocalDraft(draft)
    }

    private suspend fun loadMailBodyFromTheServer(id: Id): MailBodyEntity {
        val mailBody = api.loadElementEntity<MailBody>(id)
        val entity = MailBodyEntity(id.asString(), mailBody.compressedText ?: mailBody.text!!)
        db.mailDao().insertMailBody(entity)
        return entity
    }

    suspend fun deleteMails(folderId: IdTuple, mails: List<IdTuple>) {
        api.serviceRequestVoid(
            "tutanota", "mailservice", HttpMethod.Delete, DeleteMailData(
                _format = 0,
                folder = folderId,
                mails = mails
            )
        )
    }
}