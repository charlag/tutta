package com.charlag.tuta.compose

import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import com.charlag.tuta.*
import com.charlag.tuta.data.MailEntity
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.sys.Group
import com.charlag.tuta.entities.sys.GroupInfo
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.entities.tutanota.ConversationEntry
import com.charlag.tuta.util.FilledMutableLiveData
import com.charlag.tuta.util.combineLiveData
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class FileReference(val name: String, val size: Long, val reference: String)

typealias Attachment = FileReference

class ComposeViewModel : ViewModel() {
    private val api = DependencyDump.api
    private val mailFacade = DependencyDump.mailFacade
    private val loginFacade = DependencyDump.loginFacade
    private val contactRepo = DependencyDump.contactRepository
    private val mailSender = DependencyDump.mailSender
    private val db = DependencyDump.db
    private val userController = DependencyDump.userController

    val enabledMailAddresses: LiveData<List<String>>
    val toRecipients = FilledMutableLiveData<List<String>>(listOf())
    val ccRecipients = FilledMutableLiveData<List<String>>(listOf())
    val bccRecipients = FilledMutableLiveData<List<String>>(listOf())
    val attachments = FilledMutableLiveData<List<Attachment>>(listOf())

    val recipientTypes =
        FilledMutableLiveData<Map<String, RecipientType>>(
            mapOf()
        )
    private var localDraftId: Long = 0
    private var conversationType: ConversationType = ConversationType.NEW
    private var previousMessageId: String? = null
    private var previousMail: IdTuple? = null

    private val confidentialIsSelected: Boolean
        // For now always assume non-confidential because we don't send "external secure" emails
        // yet and if we have only internal then we will use confidential draft
        get() = false
    val willBeSentEncrypted = recipientTypes.map { types ->
        confidentialIsSelected ||
                types.isNotEmpty() && types.values.none { it == RecipientType.EXTERNAL }
    }
    val anyRecipients =
        combineLiveData(
            combineLiveData(toRecipients, ccRecipients) { toRecipients, ccRecipients ->
                toRecipients.isNotEmpty() || ccRecipients.isNotEmpty()
            },
            bccRecipients
        ) { hasOther, bccRecipients -> hasOther || bccRecipients.isNotEmpty() }

    init {
        val enabledMailAddresses = MutableLiveData<List<String>>()
        this.enabledMailAddresses = enabledMailAddresses
        viewModelScope.launch {
            try {
                val mailAddresses = userController.getEnabledMailAddresses()
                val defaultMailAddress = userController.getProps().defaultSender

                val sortedAddresses =
                    if (defaultMailAddress != null && defaultMailAddress in mailAddresses) {
                        listOf(defaultMailAddress) + (mailAddresses - defaultMailAddress)
                    } else mailAddresses
                enabledMailAddresses.postValue(sortedAddresses)
            } catch (e: Exception) {
                Log.d("ComposeVM", "Failed to load enabled mail addresses $e")
            }
        }
    }

    suspend fun send(
        subject: String,
        body: String,
        senderAddress: String
    ) {
        mailSender.send(
            loginFacade.user!!,
            prepareLocalDraft(subject, body, senderAddress, resolveRemaining = true)
        )
    }


    /**
     * @return true if saving, false if nothing to save
     */
    suspend fun saveDraft(
        subject: String,
        body: String,
        senderAddress: String
    ): Boolean {
        if (subject.isEmpty() && body.isEmpty() && attachments.value.isEmpty()) {
            return false
        }
        mailSender.save(
            loginFacade.user!!,
            prepareLocalDraft(subject, body, senderAddress, resolveRemaining = false)
        )
        return true
    }

    private suspend fun prepareLocalDraft(
        subject: String,
        body: String,
        senderAddress: String,
        resolveRemaining: Boolean
    ): LocalDraftEntity {
        val recipientTypes = this.recipientTypes.value
        val to = toRecipients.value.map {
            RecipientInfo("", it, recipientTypes.getOrDefault(it, RecipientType.UNKNOWN))
        }
        val cc = ccRecipients.value.map {
            RecipientInfo("", it, recipientTypes.getOrDefault(it, RecipientType.UNKNOWN))
        }
        val bcc = bccRecipients.value.map {
            RecipientInfo("", it, recipientTypes.getOrDefault(it, RecipientType.UNKNOWN))
        }
        val recipients = to + cc + bcc

        if (resolveRemaining) {
            this.recipientTypes.postValue(resolveRemainingRecipients(recipients, recipientTypes))
        }
        return LocalDraftEntity(
            localDraftId,
            loginFacade.user!!._id.asString(),
            subject,
            body,
            senderAddress,
            "bed",
            to,
            cc,
            bcc,
            conversationType,
            previousMessageId,
            confidential = confidentialIsSelected
                    || recipients.all { it.type == RecipientType.INTENRAL },
            replyTos = listOf(),
            files = attachments.value,
            previousMail = previousMail
        )
    }

    private suspend fun resolveRemainingRecipients(
        recipients: List<RecipientInfo>,
        recipientTypes: Map<String, RecipientType>
    ): Map<String, RecipientType> {
        val mutableRecipientTypes = recipientTypes.toMutableMap()
        for (recipient in recipients) {
            if (recipient.type == RecipientType.UNKNOWN) {
                mutableRecipientTypes[recipient.mailAddress] =
                    mailFacade.resolveRecipient(recipient.mailAddress)
            }
        }
        return mutableRecipientTypes
    }

    @MainThread
    fun addRecipient(field: RecipientField, mailAddress: String) {
        viewModelScope.launch {
            val recipientList = recipientListForField(field)
            val recipientTypes = recipientTypes.value.toMutableMap()
            val type = recipientTypes.getOrPut(mailAddress) { RecipientType.UNKNOWN }
            if (type == RecipientType.UNKNOWN) {
                recipientTypes[mailAddress] = mailFacade.resolveRecipient(mailAddress)
            }
            recipientList.value = recipientList.value + mailAddress
            this@ComposeViewModel.recipientTypes.postValue(recipientTypes)
        }
    }

    private fun recipientListForField(field: RecipientField): FilledMutableLiveData<List<String>> {
        return when (field) {
            RecipientField.TO -> toRecipients
            RecipientField.CC -> ccRecipients
            RecipientField.BCC -> bccRecipients
        }
    }

    @MainThread
    fun removeRecipient(type: RecipientField, mailAddress: String) {
        val liveData = recipientListForField(type)
        liveData.value = liveData.value - mailAddress
        if (mailAddress !in toRecipients.value &&
            mailAddress !in ccRecipients.value &&
            mailAddress !in bccRecipients.value
        ) {
            recipientTypes.mutate { it - mailAddress }
        }
    }

    @WorkerThread
    fun autocompleteMailAddress(query: String): ContactAutocompleteResult {
        // This is far from the most efficient, we should read only what we need but this'll do
        // for now.
        return contactRepo.findContacts(query).flatMap { contact ->
            contact.mailAddresses.filter {
                it.address.contains(query)
            }.map { address ->
                ContactResult(address.address, contact)
            }
        }
    }

    @MainThread
    fun addAttachment(fileReference: FileReference) {
        attachments.mutate { it + fileReference }
    }

    fun removeAttachment(file: FileReference) {
        attachments.mutate { it - file }
    }

    // All init functions should return some structure so that view doesn't have to think about it
    suspend fun initWIthLocalDraftId(id: Long): LocalDraftEntity? {
        // TODO: init things like replyto and conversationType with these things
        this.localDraftId = localDraftId
        return db.mailDao().getLocalDraft(id)
    }

    suspend fun initWithReplyMailId(replyMailId: String): MailEntity {
        val mail = db.mailDao().getMail(replyMailId)
        conversationType = ConversationType.REPLY
        val conversationEntry = api.loadListElementEntity<ConversationEntry>(mail.conversationEntry)
        previousMessageId = conversationEntry.messageId
        previousMail = IdTuple(GeneratedId(mail.listId), GeneratedId(mail.id))
        return mail
    }
}

enum class RecipientField {
    TO, CC, BCC;
}

inline fun <T> MutableLiveData<T>.mutate(block: (T?) -> T) {
    this.value = block(this.value)
}

inline fun <T> FilledMutableLiveData<T>.mutate(block: (T) -> T) {
    this.value = block(this.value)
}