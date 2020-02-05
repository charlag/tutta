package com.charlag.tuta.compose

import android.util.Log
import com.charlag.tuta.API
import com.charlag.tuta.LocalNotificationManager
import com.charlag.tuta.MailFacade
import com.charlag.tuta.data.AppDatabase
import com.charlag.tuta.data.toMail
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.sys.User
import com.charlag.tuta.files.FileHandler
import io.ktor.client.features.ClientRequestException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MailSender(
    private val mailFacade: MailFacade,
    private val fileHandler: FileHandler,
    private val notificationManager: LocalNotificationManager,
    private val db: AppDatabase,
    private val api: API
) {
    fun send(user: User, localDraft: LocalDraftEntity) {
        GlobalScope.launch {
            val localDraftId =
                if (localDraft.id == 0L) db.mailDao().insertLocalDraft(localDraft)
                else localDraft.id

            val notificationId = notificationManager.showSendingNotification()
            try {
                val draft = mailFacade.createDraft(
                    user = user,
                    subject = localDraft.subject,
                    body = localDraft.body,
                    senderAddress = localDraft.senderAddress,
                    senderName = localDraft.senderName,
                    toRecipients = localDraft.toRecipients,
                    ccRecipients = localDraft.ccRecipients,
                    bccRecipients = localDraft.bccRecipients,
                    conversationType = localDraft.conversationType,
                    previousMessageId = localDraft.previousMessageId?.let(::GeneratedId),
                    confidential = localDraft.confidential,
                    replyTos = localDraft.replyTos,
                    files = localDraft.files.map { fileHandler.uploadFile(it) }
                )
                val recipients =
                    localDraft.toRecipients + localDraft.ccRecipients + localDraft.bccRecipients
                mailFacade.sendDraft(user, draft, recipients, "en")
                db.mailDao().deleteLocalDraft(localDraft)
            } catch (e: Throwable) {
                Log.e(TAG, "Failed to send mail", e)
                notificationManager.showFailedToSendNotification(localDraftId)
            } finally {
                notificationManager.hideSendingNotification(notificationId)
            }
            if (localDraft.previousMail != null) {
                try {
                    val previousMail =
                        db.mailDao().getMail(localDraft.previousMail.elementId.asString())
                    val replyType =
                        (previousMail.replyType + localDraft.conversationType.value).coerceAtMost(3)
                    api.updateEntity(previousMail.copy(replyType = replyType).toMail())
                } catch (e: ClientRequestException) {
                    Log.w(TAG, "Failed to update previious mail $e")
                }

            }
        }
    }

    companion object {
        private const val TAG = "MailSender"
    }
}