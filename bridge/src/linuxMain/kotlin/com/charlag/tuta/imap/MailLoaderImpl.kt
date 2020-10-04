package com.charlag.tuta.imap

import UserController
import com.charlag.tuta.GroupType
import com.charlag.tuta.entities.GENERATED_MAX_ID
import com.charlag.tuta.entities.IdTuple
import com.charlag.tuta.entities.tutanota.*
import com.charlag.tuta.network.API
import kotlinx.coroutines.runBlocking

class MailLoaderImpl(
    private val api: API,
    private val userController: UserController
) : MailLoader {

    override fun mail(id: IdTuple): Mail {
        return runBlocking {
            api.loadListElementEntity(id)
        }
    }

    override fun mails(folder: MailFolder): List<Mail> {
        return runBlocking {
            api.loadRange(Mail::class, folder.mails, GENERATED_MAX_ID, 40, true)
        }
    }

    override fun folders(): List<MailFolder> {
        return runBlocking {
            val user = userController.user ?: error("not logged in")
            val mailMembership = user.memberships.first { it.groupType == GroupType.Mail.value }
            val groupRoot = api.loadElementEntity<MailboxGroupRoot>(mailMembership.group)
            val mailbox = api.loadElementEntity<MailBox>(groupRoot.mailbox)
            api.loadAll(MailFolder::class, mailbox.systemFolders!!.folders)
        }
    }

    override fun body(mail: Mail): MailBody {
        return runBlocking { api.loadElementEntity(mail.body) }
    }
}