package com.charlag.tuta.imap

import UserController
import com.charlag.mailutil.ParsedEmail
import com.charlag.mailutil.parseEmail
import com.charlag.mailutil.parseMailAddress
import com.charlag.tuta.*
import com.charlag.tuta.entities.tutanota.MailAddress
import kotlinx.coroutines.runBlocking
import kotlin.native.concurrent.ensureNeverFrozen


// Fot now just a dummy server which reads data
class SmtpServer(private val mailFacade: MailFacade, private val userController: UserController) {

    // That's not ideal and we should have a proper state machine instead
    private var expectingAuth: Boolean = false
    private var from: MailAddress? = null
    private val to = mutableListOf<MailAddress>()
    private val cc = mutableListOf<MailAddress>()
    private val bcc = mutableListOf<MailAddress>()
    private var reading: MutableList<String>? = null


    init {
        ensureNeverFrozen()
    }

    fun newConnection(): List<String> {
        return listOf("220 smtp.tutanota.com ESMTP Tutabridge")
    }

    fun respondTo(message: String): String? {
        return when {
            expectingAuth -> {
                this.expectingAuth = false
                AUTH_SUCCESS_235
            }
            reading != null -> {
                // TODO: we need to handle the case of the user-inputted line with dot
                // which should look like just two dots.
                if (message == ".") {
                    println("Finished reading")
                    try {
                        val parsedEmail = parseEmail(reading!!.joinToString("\r\n"))
                        println(parsedEmail)
                        this.sendEmail(parsedEmail)
                    } finally {
                        reading = null
                    }
                    "250 Ok"
                } else {
                    reading!! += message
                    null
                }
            }
            message.startsWith("EHLO", ignoreCase = true) -> {
                listOf(
                    "250-smtp.tutanota.com Hello ${message.substring("EHLO ".length)}",
                    "250 AUTH PLAIN",
                ).joinToString("\r\n")
            }
            message.startsWith("AUTH", ignoreCase = true) -> {
                // TODO: check error codes
                val words = message.toUpperCase().split(' ')
                val mechanism = words.getOrNull(1)?.toUpperCase()
                    ?: run {
                        println("No auth mechanism detected: $words")
                        return SYNTAX_ERROR_IN_ARGS_501
                    }
                when (mechanism) {
                    "PLAIN" -> {
                        if (words.size > 2) {
                            AUTH_SUCCESS_235
                        } else {
                            this.expectingAuth = true
                            "334 "
                        }
                    }
                    else -> {
                        println("Unknown auth mechanism: $mechanism")
                        COMMAND_PARAMETER_NOT_IMPLEMENTED_504
                    }
                }
            }
            message.startsWith("HELO", ignoreCase = true) -> {
                val otherParty = message.split(" ")[1]
                "HELO $otherParty, I am glad to see you"
            }
            message.startsWith("DATA", ignoreCase = true) -> {
                reading = mutableListOf()
                "354 <CR><LF>.<CR><LF>"
            }
            message.startsWith("MAIL FROM:", ignoreCase = true) -> {
                val address = parseMailAddress(message.substring("MAIL FROM:".length).trim())
                this.from = address.toAddress()
                OK_250
            }
            message.startsWith("RCPT TO:", ignoreCase = true) -> {
                val address =
                    parseMailAddress(message.substring("RCPT TO:".length).trim('\r', '\n', ' '))
                this.to += address.toAddress()
                OK_250
            }
            message.startsWith("QUIT", ignoreCase = true) -> {
                "221 Bye"
            }
            else -> OK_250
        }
    }

    private fun sendEmail(parsedEmail: ParsedEmail) {
        runBlocking {
            println("Creating draft...")
            val toRecipients = to.map { resolveMailAddress(it) }
            val ccRecipients = cc.map { resolveMailAddress(it) }
            val bccRecipients = bcc.map { resolveMailAddress(it) }
            val allRecipients = toRecipients + ccRecipients + bccRecipients
            val confidential = allRecipients.all { it.type == RecipientType.INTENRAL }

            val user = userController.user!!
            // TODO: process files
            val draft = mailFacade.createDraft(
                user = user,
                subject = parsedEmail.headers["Subject"] ?: "",
                body = parsedEmail.parsedBody.text,
                senderAddress = from!!.address,
                senderName = from!!.name,
                toRecipients = toRecipients,
                ccRecipients = ccRecipients,
                bccRecipients = bccRecipients,
                conversationType = ConversationType.NEW,
                previousMessageId = null,
                files = listOf(),
                confidential = confidential,
                replyTos = listOf()
            )
            println("Created draft, sending...")
            mailFacade.sendDraft(user, draft, allRecipients, "en")
            println("Email sent!")
        }
    }

    private suspend fun resolveMailAddress(mailAddress: MailAddress): RecipientInfo {
        val type = mailFacade.resolveRecipient(mailAddress.address)
        return RecipientInfo(
            name = mailAddress.name,
            mailAddress = mailAddress.address,
            type = type,
        )
    }

    companion object {
        private const val AUTH_SUCCESS_235 = "235 2.7.0 Authentication Successful"
        private const val OK_250 = "250 Ok"
        private const val SYNTAX_ERROR_IN_ARGS_501 = "501 "
        private const val COMMAND_PARAMETER_NOT_IMPLEMENTED_504 = "504 "
    }

    private fun com.charlag.mailutil.MailAddress.toAddress(): MailAddress {
        return MailAddress(address = address, name = name, contact = null)
    }
}
