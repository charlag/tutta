package com.charlag.tuta.compose

import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import com.charlag.tuta.*
import com.charlag.tuta.entities.sys.Group
import com.charlag.tuta.entities.sys.GroupInfo
import com.charlag.tuta.util.FilledMutableLiveData
import kotlinx.coroutines.launch

class ComposeViewModel : ViewModel() {
    private val api = DependencyDump.api
    private val mailFacade = DependencyDump.mailFacade
    private val loginFacade = DependencyDump.loginFacade
    private val contactRepo = DependencyDump.contactRepository

    val enabledMailAddresses: LiveData<List<String>>
    val toRecipients = FilledMutableLiveData<List<String>>(listOf())
    val ccRecipients = FilledMutableLiveData<List<String>>(listOf())
    val bccRecipients = FilledMutableLiveData<List<String>>(listOf())

    val recipientTypes =
        FilledMutableLiveData<Map<String, RecipientType>>(
            mapOf()
        )

    private val confidentialIsSelected: Boolean
        // For now always assume non-confidential because we don't send "external secure" emails
        // yet and if we have only internal then we will use confidential draft
        get() = false
    val willBeSentEncrypted = recipientTypes.map { types ->
        confidentialIsSelected ||
                types.isNotEmpty() && types.values.none { it == RecipientType.EXTERNAL }
    }

    init {
        val enabledMailAddresses = MutableLiveData<List<String>>()
        this.enabledMailAddresses = enabledMailAddresses
        viewModelScope.launch {
            try {
                val user = loginFacade.waitForLogin()
                val userGroupInfo = api.loadListElementEntity<GroupInfo>(user.userGroup.groupInfo)
                val mailGroupMembership =
                    user.memberships.first { it.groupType == GroupType.Mail.value }
                val mailGroup = api.loadElementEntity<Group>(mailGroupMembership.group)
                enabledMailAddresses.postValue(
                    mailFacade.getEnabledMailAddresses(
                        user,
                        userGroupInfo,
                        mailGroup
                    )
                )
            } catch (e: Exception) {
                Log.d("ComposeVM", "Failed to load enabled mail addresses $e")
            }
        }
    }

    suspend fun send(
        subject: String,
        body: String,
        senderAddress: String
    ): Boolean {
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
        if (recipients.isEmpty()) {
            return false
        }

        this.recipientTypes.postValue(resolveRemainingRecipients(recipients, recipientTypes))

        val draft = mailFacade.createDraft(
            user = loginFacade.user!!,
            subject = subject,
            body = body,
            senderAddress = senderAddress,
            senderName = "bed",
            toRecipients = to,
            ccRecipients = cc,
            bccRecipients = bcc,
            conversationType = ConversationType.NEW,
            previousMessageId = null,
            confidential = confidentialIsSelected
                    || recipients.all { it.type == RecipientType.INTENRAL },
            replyTos = listOf()
        )

        mailFacade.sendDraft(loginFacade.user!!, draft, recipients, "en")
        return true
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