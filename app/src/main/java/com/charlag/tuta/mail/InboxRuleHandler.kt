package com.charlag.tuta.mail

import android.util.Log
import com.charlag.tuta.InboxRuleType
import com.charlag.tuta.MailFacade
import com.charlag.tuta.MailFolderType
import com.charlag.tuta.UserController
import com.charlag.tuta.entities.tutanota.InboxRule
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailAddress
import com.charlag.tuta.entities.tutanota.MailFolder
import io.ktor.client.features.ClientRequestException

// This could stay in SharedCode or at least some logic.
// Consider pulling this out.

interface InboxRuleHandler {
    suspend fun findAndApplyMatchingRule(folders: List<MailFolder>, mail: Mail)
}

class RealInboxRuleHandler(
    private val userController: UserController,
    private val mailFacade: MailFacade
) : InboxRuleHandler {
    override suspend fun findAndApplyMatchingRule(folders: List<MailFolder>, mail: Mail) {
        if (!mail.unread) {
            return
        }
        val folder = folders.find { it.folderType == MailFolderType.INBOX.value }
        if (folder == null || folder.mails != mail._id!!.listId) {
            return
        }
        findMatchingRule(mail)
            ?.let { rule -> folders.find { it._id == rule.targetFolder } }
            ?.let { targetFolder ->
                try {
                    mailFacade.moveMails(listOf(mail._id!!), targetFolder._id!!)
                } catch (e: ClientRequestException) {
                    Log.d("InboxRule", "Server locked the list $e")
                }
            }
    }

    private suspend fun findMatchingRule(mail: Mail): InboxRule? {
        val inboxRules = userController.getProps().inboxRules
        return inboxRules.firstOrNull { rule ->
            when (rule.type) {
                InboxRuleType.FROM_EQUALS.raw ->
                    checkMailAddresses(listOf(mail.sender), rule)
                InboxRuleType.RECIPIENT_TO_EQUALS.raw ->
                    checkMailAddresses(mail.toRecipients, rule)
                InboxRuleType.RECIPIENT_CC_EQUALS.raw ->
                    checkMailAddresses(mail.ccRecipients, rule)
                InboxRuleType.RECIPIENT_BCC_EQUALS.raw ->
                    checkMailAddresses(mail.bccRecipients, rule)
                InboxRuleType.SUBJECT_CONTAINS.raw ->
                    checkContainsRule(mail.subject, rule)
                // TODO
                InboxRuleType.MAIL_HEADER_CONTAINS.raw -> false
                else -> false
            }
        }
    }

    private fun checkMailAddresses(addresses: List<MailAddress>, inboxRule: InboxRule): Boolean {
        return addresses.any { address ->
            // Should check regex too or at least a domain
            address.address.endsWith(inboxRule.value)
        }
    }

    private fun checkContainsRule(value: String, inboxRule: InboxRule): Boolean {
        // TODO: regex
        return value.contains(inboxRule.value)
    }
}