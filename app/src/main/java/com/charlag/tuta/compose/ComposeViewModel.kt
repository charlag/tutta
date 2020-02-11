package com.charlag.tuta.compose

import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import com.charlag.tuta.*
import com.charlag.tuta.data.MailAddressEntity
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.entities.tutanota.ConversationEntry
import com.charlag.tuta.entities.tutanota.File
import com.charlag.tuta.mail.MailRepository
import com.charlag.tuta.util.FilledMutableLiveData
import com.charlag.tuta.util.combineLiveData
import com.charlag.tuta.util.mutate
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.io.FileNotFoundException
import java.io.IOException


sealed class DraftFile {
    abstract val name: String
    abstract val size: Long
}

@Serializable
data class FileReference(
    override val name: String,
    override val size: Long,
    val reference: String
) : DraftFile()

data class RemoteFile(
    override val name: String,
    override val size: Long,
    val fileId: IdTuple
) :
    DraftFile()

class ComposeViewModel : ViewModel() {
    private val api = DependencyDump.api
    private val mailFacade = DependencyDump.mailFacade
    private val loginFacade = DependencyDump.loginFacade
    private val contactRepo = DependencyDump.contactRepository
    private val mailSender = DependencyDump.mailSender
    private val userController = DependencyDump.userController
    private val mailRepository: MailRepository = DependencyDump.mailRepository
    private val fileHandler = DependencyDump.fileHandler

    val enabledMailAddresses: LiveData<List<String>>
    val toRecipients = FilledMutableLiveData<List<String>>(listOf())
    val ccRecipients = FilledMutableLiveData<List<String>>(listOf())
    val bccRecipients = FilledMutableLiveData<List<String>>(listOf())
    private val pickedFiles = FilledMutableLiveData<List<FileReference>>(listOf())
    private val remoteFiles = FilledMutableLiveData<List<RemoteFile>>(listOf())
    val attachments = combineLiveData(pickedFiles, remoteFiles) { picked, remote ->
        picked + remote
    }
    val recipientTypes = FilledMutableLiveData<Map<String, RecipientType>>(mapOf())

    private var localDraftId: Long = 0
    private var conversationType: ConversationType = ConversationType.NEW
    private var previousMessageId: String? = null
    private var previousMail: IdTuple? = null
    private var replyContent: String? = null
    private var loadExternalContent = false
    private var draftId: IdTuple? = null

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
        body: Spanned,
        senderAddress: String
    ) {
        val text = Html.toHtml(body, 0)
        mailSender.send(
            loginFacade.user!!,
            prepareLocalDraft(subject, text, senderAddress, resolveRemaining = true)
        )
    }


    /**
     * @return true if saving, false if nothing to save
     */
    suspend fun saveDraft(
        subject: String,
        body: Spanned,
        senderAddress: String?
    ): Boolean {
        // Should check if they are changed
        if (subject.isEmpty() && body.isEmpty() && attachments.value!!.isEmpty()) {
            return false
        }
        val text = Html.toHtml(body, 0)
        mailSender.save(
            loginFacade.user!!,
            prepareLocalDraft(subject, text, senderAddress, resolveRemaining = false)
        )
        return true
    }

    private suspend fun prepareLocalDraft(
        subject: String,
        body: String,
        senderAddress: String?,
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
            this.recipientTypes.postValue(resolveRecipients(recipients, recipientTypes))
        }
        val sender = senderAddress
            ?: userController.getProps().defaultSender
            ?: userController.getEnabledMailAddresses().first()
        return LocalDraftEntity(
            localDraftId,
            loginFacade.user!!._id.asString(),
            subject,
            body,
            sender,
            userController.getUserGroupInfo().name,
            to,
            cc,
            bcc,
            conversationType,
            previousMessageId,
            confidential = confidentialIsSelected
                    || recipients.all { it.type == RecipientType.INTENRAL },
            replyTos = listOf(),
            files = pickedFiles.value,
            previousMail = previousMail,
            replyContent = replyContent,
            loadExternalContent = loadExternalContent,
            remoteDraftId = draftId,
            remoteFiles = remoteFiles.value.map { it.fileId }
        )
    }

    private suspend fun resolveRecipients(
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

    private fun resolveRecipientsAsync(recipients: List<RecipientInfo>) {
        viewModelScope.launch {
            val resolved = resolveRecipients(
                recipients,
                recipientTypes.value
            )
            recipientTypes.mutate { it + resolved }
        }
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
        viewModelScope.launch {
            val copied = fileHandler.copyFilesToTempDir(fileReference)
            pickedFiles.mutate { it + copied }
        }
    }

    fun removeAttachment(file: DraftFile) {
        when (file) {
            is FileReference -> pickedFiles.mutate { it - file }
            is RemoteFile -> remoteFiles.mutate { it - file }
        }
    }

    // All init functions should return some structure so that view doesn't have to think about it
    suspend fun initWIthLocalDraftId(id: Long): InitData? {
        this.localDraftId = id
        return mailRepository.getLocalDraft(id)?.let { draft ->
            conversationType = draft.conversationType
            previousMessageId = draft.previousMessageId
            previousMail = draft.previousMail
            loadExternalContent = draft.loadExternalContent
            // TODO: init things like replyTos
            InitData(
                subject = draft.subject,
                content = SpannableString(draft.body),
                toRecipients = draft.toRecipients,
                ccRecipients = draft.ccRecipients,
                bccRecipients = draft.bccRecipients,
                replyContent = null,
                loadExternalContent = draft.loadExternalContent
            )
        }
    }

    suspend fun initWithReplyInitData(replyInitData: ReplyInitData): InitData {
        val mail = mailRepository.getMail(
            IdTuple.fromRawValues(
                replyInitData.listId,
                replyInitData.mailId
            )
        )

        conversationType = ConversationType.REPLY
        val conversationEntry = api.loadListElementEntity<ConversationEntry>(mail.conversationEntry)
        previousMessageId = conversationEntry.messageId
        previousMail = IdTuple(GeneratedId(mail.listId), GeneratedId(mail.id))
        loadExternalContent = replyInitData.loadExternalContent

        val body = mailRepository.getMailBody(mail.body)
        replyContent = body.text

        // TODO: handle reply all
        val toRecipients =
            listOf(
                RecipientInfo(mail.sender.name, mail.sender.address, RecipientType.UNKNOWN)
            )
        viewModelScope.launch {
            val oldValue = recipientTypes.value
            recipientTypes.value =
                resolveRecipients(toRecipients, oldValue)
        }
        val subject =
            if (mail.subject.startsWith("Re:", ignoreCase = true)) mail.subject
            else "Re: " + mail.subject
        return InitData(
            subject = subject,
            content = SpannableString(""),
            replyContent = replyContent,
            toRecipients = toRecipients,
            ccRecipients = listOf(),
            bccRecipients = listOf(),
            loadExternalContent = loadExternalContent
        )
    }

    suspend fun initWithForwardData(forwardInitData: ForwardInitData): InitData {
        val mail = mailRepository.getMail(
            IdTuple.fromRawValues(
                forwardInitData.listId,
                forwardInitData.mailId
            )
        )

        conversationType = ConversationType.FORWARD
        val conversationEntry = api.loadListElementEntity<ConversationEntry>(mail.conversationEntry)
        previousMessageId = conversationEntry.messageId
        previousMail = IdTuple(GeneratedId(mail.listId), GeneratedId(mail.id))
        loadExternalContent = forwardInitData.loadExternalContent

        val body = mailRepository.getMailBody(mail.body)
        replyContent = body.text

        val subject = "FWD ${mail.subject}"
        return InitData(
            subject = subject,
            content = SpannableString(""),
            replyContent = replyContent,
            toRecipients = listOf(),
            ccRecipients = listOf(),
            bccRecipients = listOf(),
            loadExternalContent = loadExternalContent
        )
    }

    suspend fun initWithDraftData(draftInitData: DraftInitData): InitData {
        val mail = mailRepository.getMail(
            IdTuple.fromRawValues(
                draftInitData.listId,
                draftInitData.draftId
            )
        )
        this.draftId = IdTuple(GeneratedId(mail.listId), GeneratedId(mail.id))
        viewModelScope.launch {
            val loaded = mail.attachments.mapNotNull {
                try {
                    val file = api.loadListElementEntity<File>(it)
                    RemoteFile(file.name, file.size, file._id)
                } catch (e: FileNotFoundException) {
                    null
                } catch (e: IOException) {
                    null
                }
            }
            remoteFiles.postValue(loaded)
        }


        val conversationEntry = api.loadListElementEntity<ConversationEntry>(mail.conversationEntry)
        conversationType = ConversationType.fromRaw(conversationEntry.conversationType)

        conversationEntry.previous?.let { previous ->
            val previousConversationEntry =
                api.loadListElementEntity<ConversationEntry>(previous)
            previousMessageId = previousConversationEntry.messageId
            previousMail = previousConversationEntry.mail
        }

        val body = mailRepository.getMailBody(mail.body)

        return InitData(
            subject = mail.subject,
            content = Html.fromHtml(body.text, 0),
            replyContent = replyContent,
            toRecipients = mail.toRecipients.map { it.toRecipientInfo() },
            ccRecipients = mail.ccRecipients.map { it.toRecipientInfo() },
            bccRecipients = mail.bccRecipients.map { it.toRecipientInfo() },
            loadExternalContent = false // for now
        ).also {
            resolveRecipientsAsync(it.toRecipients + it.ccRecipients + it.bccRecipients)
        }
    }

    private fun MailAddressEntity.toRecipientInfo() = RecipientInfo(
        name = name,
        mailAddress = address,
        type = RecipientType.UNKNOWN
    )
}

data class InitData(
    val subject: String,
    val content: Spanned,
    val replyContent: String?,
    val toRecipients: List<RecipientInfo>,
    val ccRecipients: List<RecipientInfo>,
    val bccRecipients: List<RecipientInfo>,
    val loadExternalContent: Boolean
)

enum class RecipientField {
    TO, CC, BCC;
}

