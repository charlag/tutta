package com.charlag.tuta.compose

import android.util.Log
import androidx.lifecycle.*
import com.charlag.tuta.*
import com.charlag.tuta.entities.sys.Group
import com.charlag.tuta.entities.sys.GroupInfo
import kotlinx.coroutines.launch

class ComposeViewModel : ViewModel() {
    private val api = DependencyDump.api
    private val mailFacade = DependencyDump.mailFacade
    private val loginFacade = DependencyDump.loginFacade

    val enabledMailAddresses: LiveData<List<String>>
    val toRecipients = MutableLiveData<List<String>>(listOf())
    val ccRecipients = MutableLiveData<List<String>>(listOf())
    val bccRecipients = MutableLiveData<List<String>>(listOf())

    init {
        val enabledMailAddresses = MutableLiveData<List<String>>()
        this.enabledMailAddresses = enabledMailAddresses
        viewModelScope.launch {
            try {
                val user = loginFacade.waitForLogin()
                val userGroupinfo = api.loadListElementEntity<GroupInfo>(user.userGroup.groupInfo)
                val mailGroupMembership =
                    user.memberships.first { it.groupType == GroupType.Mail.value }
                val mailGroup = api.loadElementEntity<Group>(mailGroupMembership.group)
                enabledMailAddresses.postValue(
                    mailFacade.getEnabledMailAddresses(
                        user,
                        userGroupinfo,
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
        // All internal for now
        val to = toRecipients.value!!.map { RecipientInfo("", it, RecipientType.INTENRAL) }
        val cc = ccRecipients.value!!.map { RecipientInfo("", it, RecipientType.INTENRAL) }
        val bcc = bccRecipients.value!!.map { RecipientInfo("", it, RecipientType.INTENRAL) }
        val recipients = to + cc + bcc
        if (recipients.isEmpty()) {
            return false
        }
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
            confidential = true,
            replyTos = listOf()
        )

        mailFacade.sendDraft(loginFacade.user!!, draft, recipients, "en")
        return true
    }

    fun addRecipient(type: RecipientField, mailAddress: String) {
        // TODO: start checking if internal recipient here
        val liveData = liveDataForField(type)
        liveData.value = liveData.value!! + mailAddress
    }

    private fun liveDataForField(type: RecipientField): MutableLiveData<List<String>> {
        return when (type) {
            RecipientField.TO -> toRecipients
            RecipientField.CC -> ccRecipients
            RecipientField.BCC -> bccRecipients
        }
    }

    fun removeRecipient(type: RecipientField, mailAddress: String) {
        val liveData = liveDataForField(type)
        liveData.value = liveData.value!! - mailAddress
    }
}

enum class RecipientField {
    TO, CC, BCC;
}