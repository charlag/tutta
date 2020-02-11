package com.charlag.tuta.compose

import android.util.Log
import com.charlag.tuta.LocalNotificationManager
import com.charlag.tuta.MailFacade
import com.charlag.tuta.data.toMail
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.sys.User
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.files.FileHandler
import com.charlag.tuta.mail.MailRepository
import com.charlag.tuta.network.API
import io.ktor.client.features.ClientRequestException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MailSender(
    private val mailFacade: MailFacade,
    private val fileHandler: FileHandler,
    private val notificationManager: LocalNotificationManager,
    private val mailRepository: MailRepository,
    private val api: API
) {
    fun send(user: User, localDraft: LocalDraftEntity) {
        GlobalScope.launch {
            val notificationId = notificationManager.showSendingNotification()
            val localDraftId = saveLocalDraft(localDraft)
            try {
                val draft = createOrUpdateRemoveDraft(user, localDraft)
                val recipients =
                    localDraft.toRecipients + localDraft.ccRecipients + localDraft.bccRecipients
                mailFacade.sendDraft(user, draft, recipients, "en")
                mailRepository.deleteLocalDraft(localDraft)
            } catch (e: Throwable) {
                Log.e(TAG, "Failed to send mail", e)
                notificationManager.showFailedToSendNotification(localDraftId)
            } finally {
                notificationManager.hideSendingNotification(notificationId)
            }
            if (localDraft.previousMail != null) {
                try {
                    val previousMail = mailRepository.getMail(localDraft.previousMail)
                    val replyType =
                        (previousMail.replyType + localDraft.conversationType.value).coerceAtMost(3)
                    api.updateEntity(previousMail.copy(replyType = replyType).toMail())
                } catch (e: ClientRequestException) {
                    Log.w(TAG, "Failed to update previious mail $e")
                }
            }
        }
    }

    fun save(user: User, localDraft: LocalDraftEntity) {
        GlobalScope.launch {
            val localDraftId =
                saveLocalDraft(localDraft)
            try {
                if (localDraft.remoteDraftId != null) {
                    updateRemoteDraft(user, localDraft)
                } else {
                    createRemoteDraft(user, localDraft)
                }
            } catch (e: Exception) {
                notificationManager.showFailedToSendNotification(localDraftId)
            }
        }
    }

    private suspend fun createOrUpdateRemoveDraft(user: User, localDraft: LocalDraftEntity): Mail {
        return if (localDraft.remoteDraftId != null) {
            updateRemoteDraft(user, localDraft)
        } else {
            createRemoteDraft(user, localDraft)
        }
    }

    private suspend fun saveLocalDraft(localDraft: LocalDraftEntity) =
        if (localDraft.id == 0L) mailRepository.saveLocalDraft(localDraft)
        else localDraft.id

    private suspend fun createRemoteDraft(user: User, localDraft: LocalDraftEntity): Mail {
        val additionalContent = localDraft.replyContent?.let { "\n" + it } ?: ""
        val body = localDraft.body + additionalContent
        return mailFacade.createDraft(
            user = user,
            subject = localDraft.subject,
            body = body,
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
    }

    private suspend fun updateRemoteDraft(user: User, localDraft: LocalDraftEntity): Mail {
        val additionalContent = localDraft.replyContent?.let { "\n" + it } ?: ""
        val body = localDraft.body + additionalContent
        val oldRemoteDraft = api.loadListElementEntity<Mail>(localDraft.remoteDraftId!!)
        return mailFacade.updateDraft(
            user = user,
            subject = localDraft.subject,
            body = body,
            senderAddress = localDraft.senderAddress,
            senderName = localDraft.senderName,
            toRecipients = localDraft.toRecipients,
            ccRecipients = localDraft.ccRecipients,
            bccRecipients = localDraft.bccRecipients,
            confidential = localDraft.confidential,
            uploadedFiles = localDraft.files.map { fileHandler.uploadFile(it) },
            remoteFiles = localDraft.remoteFiles,
            draft = oldRemoteDraft
        )
    }

    companion object {
        private const val TAG = "MailSender"
    }
}