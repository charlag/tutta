package com.charlag.tuta.imap

import UserController
import com.charlag.tuta.GroupType
import com.charlag.tuta.MailDb
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.tutanota.*
import com.charlag.tuta.network.API
import kotlinx.coroutines.runBlocking
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze

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
    override fun uid(mail: Mail): Int {
        // UIDs must be increasing and must be 32bit
        // We are takng a second now. We should change this and probably persist the mapping.
        return (mail.receivedDate.millis / 1000).toInt()
    }

    override fun numberOfMailsFor(folder: MailFolder): Int {
        return mailsDb.count(folder.mails.asString())
    }

    override fun numberOfUneadFor(folder: MailFolder): Int {
        // This is a placeholder, we should probably take count form the server or at least ask
        // database to do this
        return this.mailsDb.readMultiple(folder.mails.asString(), fromUid = 0, toUid = null)
            .count { it.unread }
    }


    override fun folders(): List<MailFolder> {
        return mailsDb.readFolders()
    }

    /**
     * This is a stub impl which only loads last emails.
     */
    private fun getMailsFor(folder: MailFolder): List<Mail> {
        // read all for now
        return mailsDb.readMultiple(folder.mails.asString(), fromUid = 0, toUid = null)
    }

    override fun mailBySeq(folder: MailFolder, seq: Int): Mail? {
        return getMailsFor(folder).getOrNull(seq - 1)
    }

    override fun mailByUid(folder: MailFolder, uid: Int): Mail? {
        return mailsDb.readSingle(folder.mails.asString(), uid)
    }

    override fun mailsBySeq(folder: MailFolder, start: Int, end: Int?): List<Mail> {
        val mails = getMailsFor(folder)
        val endIndex = end?.let { it - 1 } ?: mails.lastIndex
        return mails.slice(start - 1..endIndex)
    }

    override fun mailsByUid(folder: MailFolder, startUid: Int, endUid: Int?): List<Mail> {
        return mailsDb.readMultiple(folder.mails.asString(), startUid, endUid)
    }

    override fun body(mail: Mail): MailBody {
        return runBlocking { api.loadElementEntity(mail.body) }
    }
}