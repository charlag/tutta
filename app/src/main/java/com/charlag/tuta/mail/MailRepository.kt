package com.charlag.tuta.mail

import com.charlag.tuta.compose.LocalDraftEntity
import com.charlag.tuta.data.*
import com.charlag.tuta.network.API
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailBody
import com.charlag.tuta.entities.tutanota.MoveMailData
import io.ktor.http.HttpMethod

class MailRepository(
    private val api: API,
    private val db: AppDatabase
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
        return db.mailDao().getMail(id.elementId.asString())
            ?: api.loadListElementEntity<Mail>(id).let {
                val entity = it.toEntity()
                db.mailDao().insertMail(entity)
                return entity
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
}