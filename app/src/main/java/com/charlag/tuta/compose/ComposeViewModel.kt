package com.charlag.tuta.compose

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charlag.tuta.ConversationType
import com.charlag.tuta.DependencyDump
import com.charlag.tuta.GroupType
import com.charlag.tuta.RecipientInfo
import com.charlag.tuta.entities.sys.Group
import com.charlag.tuta.entities.sys.GroupInfo
import kotlinx.coroutines.launch

class ComposeViewModel : ViewModel() {
    private val api = DependencyDump.api
    private val mailFacade = DependencyDump.mailFacade
    private val loginFacade = DependencyDump.loginFacade

    val enabledMailAddresses: LiveData<List<String>>

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
        senderAddress: String,
        recipients: List<RecipientInfo>
    ) {
        val draft = mailFacade.createDraft(
            user = loginFacade.user!!,
            subject = subject,
            body = body,
            senderAddress = senderAddress,
            senderName = "bed",
            toRecipients = recipients,
            ccRecipients = listOf(),
            bccRecipients = listOf(),
            conversationType = ConversationType.NEW,
            previousMessageId = null,
            confidential = true,
            replyTos = listOf()
        )

        val allRecipients = draft.toRecipients + draft.ccRecipients + draft.bccRecipients

        mailFacade.sendDraft(loginFacade.user!!, draft, recipients, "en")
    }
}