package com.charlag.tuta.compose

import android.util.Log
import com.charlag.tuta.LocalNotificationManager
import com.charlag.tuta.MailFacade
import com.charlag.tuta.data.AppDatabase
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.sys.User
import com.charlag.tuta.files.FileHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MailSender(
    private val mailFacade: MailFacade,
    private val fileHandler: FileHandler,
    private val notificationManager: LocalNotificationManager,
    private val db: AppDatabase
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
                Log.e("MailSender", "Failed to send mail", e)
                notificationManager.showFailedToSendNotification(localDraftId)
            } finally {
                notificationManager.hideSendingNotification(notificationId)
            }
        }
    }
}