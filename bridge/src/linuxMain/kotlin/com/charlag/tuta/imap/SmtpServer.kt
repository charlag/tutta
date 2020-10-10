package com.charlag.tuta.imap

import UserController
import com.charlag.tuta.ConversationType
import com.charlag.tuta.MailFacade
import com.charlag.tuta.RecipientInfo
import com.charlag.tuta.RecipientType
import com.charlag.tuta.entities.tutanota.MailAddress
import kotlinx.coroutines.runBlocking
import kotlin.native.concurrent.ensureNeverFrozen


// Fot now just a dummy server which reads data
class SmtpServer(private val mailFacade: MailFacade, private val userController: UserController) {
    init {
        ensureNeverFrozen()
    }

    fun newConnection(): List<String> {
        return listOf("220 smtp.tutanota.com ESMTP Tutabridge")
    }

    private var from: MailAddress? = null
    private val to = mutableListOf<MailAddress>()
    private val cc = mutableListOf<MailAddress>()
    private val bcc = mutableListOf<MailAddress>()
    private var reading: MutableList<String>? = null

    private val parseMailAddress = mailAddressParser.build()

    fun respondTo(message: String): String? {
        return when {
            message.startsWith("HELO") -> {
                val otherParty = message.split(" ")[1]
                "HELO $otherParty, I am glad to see you"
            }
            message.startsWith("DATA") -> {
                reading = mutableListOf()
                "354 <CR><LF>.<CR><LF>"
            }
            message.startsWith("MAIL FROM:") -> {
                val address = parseMailAddress(message.substring("MAIL FROM:".length).trim())
                this.from = address
                OK_250
            }
            message.startsWith("RCPT TO:") -> {
                val address =
                    parseMailAddress(message.substring("RCPT TO:".length).trim('\r', '\n', ' '))
                this.to += address
                OK_250
            }
            reading != null -> {
                reading!! += message
                // TODO: we need to handle the case of the user-inputted line with dot
                // which should look like just two dots.
                if (message.contains(".\r\n")) {
                    println("Finished reading")
                    val parsedEmail = parseEmail(reading!!.joinToString("\n"))
                    println(parsedEmail)
                    reading = null
                    this.sendEmail(parsedEmail)
                    "250 Ok"
                } else {
                    null
                }
            }
            message.startsWith("QUIT") -> {
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
            val user = userController.user!!
            val draft = mailFacade.createDraft(
                user = user,
                subject = parsedEmail.headers["Subject"] ?: "",
                body = parsedEmail.body,
                senderAddress = from!!.address,
                senderName = from!!.name,
                toRecipients = toRecipients,
                ccRecipients = ccRecipients,
                bccRecipients = bccRecipients,
                conversationType = ConversationType.NEW,
                previousMessageId = null,
                files = listOf(),
                confidential = true,
                replyTos = listOf()
            )
            println("Created draft, sending...")
            val allRecipients = toRecipients + ccRecipients + bccRecipients
            mailFacade.sendDraft(user, draft, allRecipients, "en")
            println("Email sent!")
        }
    }

    private fun resolveMailAddress(mailAddress: MailAddress): RecipientInfo {
        // TODO: resolve recipients
        return RecipientInfo(
            name = mailAddress.name,
            mailAddress = mailAddress.address,
            RecipientType.INTENRAL
        )
    }
}

private const val OK_250 = "250 Ok"