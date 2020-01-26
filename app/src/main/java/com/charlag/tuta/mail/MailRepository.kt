package com.charlag.tuta.mail

import com.charlag.tuta.API
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.entities.tutanota.MoveMailData
import io.ktor.http.HttpMethod

class MailRepository(
    private val api: API
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
}