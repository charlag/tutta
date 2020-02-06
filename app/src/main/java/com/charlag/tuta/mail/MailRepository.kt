package com.charlag.tuta.mail

import com.charlag.tuta.network.API
import com.charlag.tuta.data.MailBodyEntity
import com.charlag.tuta.data.MailDao
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.entities.tutanota.MailBody
import com.charlag.tuta.entities.tutanota.MoveMailData
import io.ktor.http.HttpMethod

class MailRepository(
    private val api: API,
    private val mailDao: MailDao
) {
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

    suspend fun getMailBody(id: Id): MailBodyEntity {
        return mailDao.getMailBody(id.asString()) ?: loadMailBodyFromTheServer(id)
    }

    private suspend fun loadMailBodyFromTheServer(id: Id): MailBodyEntity {
        val mailBody = api.loadElementEntity<MailBody>(id)
        val entity = MailBodyEntity(id.asString(), mailBody.compressedText ?: mailBody.text!!)
        mailDao.insertMailBody(entity)
        return entity
    }
}