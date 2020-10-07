package com.charlag.tuta.imap

import UserController
import com.charlag.tuta.GroupType
import com.charlag.tuta.MailDb
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.tutanota.*
import com.charlag.tuta.network.API
import kotlinx.coroutines.runBlocking

/**
 * This is a stub implementation which loads only the first batch of emails.
 * To give truthful result we would probably need to load the whole folder from the bottom because
 * we need sequential IDs and we don't even know the number of emails. If we did know the number we
 * could just count backwards. But if we load everything it makes sense to persist as well.
 */
class MailLoaderImpl(
    private val api: API,
    private val userController: UserController,
    private val mailsDb: MailDb,
) : MailLoader {
    private val uidIndex = mutableMapOf<Int, Id>()
    private var cachedFolders: List<MailFolder>? = null

    override fun uid(mail: Mail): Int {
        // UIDs must be increasing and must be 32bit
        // We are takng a second now. We should change this and probably persist the mapping.
        val uid = (mail.receivedDate.millis / 1000).toInt()
        uidIndex[uid] = mail.getId().elementId
        return uid
    }

    override fun numberOfMailsFor(folder: MailFolder): Int {
        return mailsDb.count() //getMailsFor(folder).size
    }

    override fun folders(): List<MailFolder> {
        return cachedFolders ?: runBlocking {
            val user = userController.user ?: error("not logged in")
            val mailMembership = user.memberships.first { it.groupType == GroupType.Mail.value }
            val groupRoot = api.loadElementEntity<MailboxGroupRoot>(mailMembership.group)
            val mailbox = api.loadElementEntity<MailBox>(groupRoot.mailbox)
            api.loadAll(MailFolder::class, mailbox.systemFolders!!.folders)
        }.also { this.cachedFolders = it }
    }

    /**
     * This is a stub impl which only loads last emails.
     */
    private fun getMailsFor(folder: MailFolder): List<Mail> {
        // read all for now
        return mailsDb.readMultiple(fromUid = 0, toUid = null)
    }

    override fun mailBySeq(folder: MailFolder, seq: Int): Mail? {
        return getMailsFor(folder).getOrNull(seq - 1)
    }

    override fun mailByUid(folder: MailFolder, uid: Int): Mail? {
        return mailsDb.readSingle(uid)
    }

    override fun mailsBySeq(folder: MailFolder, start: Int, end: Int?): List<Mail> {
        val mails = getMailsFor(folder)
        val endIndex = end?.let { it - 1 } ?: mails.lastIndex
        return mails.slice(start - 1..endIndex)
    }

    override fun mailsByUid(folder: MailFolder, startUid: Int, endUid: Int?): List<Mail> {
        return mailsDb.readMultiple(startUid, endUid)
    }

    private fun uidToId(uid: Int): Id? {
        val id = uidIndex[uid]
        if (id == null) {
            println("Did not find for uid: $uid")
        }
        return id
    }

    override fun body(mail: Mail): MailBody {
        return runBlocking { api.loadElementEntity(mail.body) }
    }
}