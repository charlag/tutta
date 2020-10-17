package com.charlag.tuta.imap

import com.charlag.tuta.FileFacade
import com.charlag.tuta.MailDb
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.tutanota.File
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailBody
import com.charlag.tuta.entities.tutanota.MailFolder
import com.charlag.tuta.getId
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
    private val mailsDb: MailDb,
    private val fileFacade: FileFacade,
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

    override fun nextUid(folder: MailFolder): Int {
        return (mailsDb.lastMail(folder.mails.asString()) ?: 0) + 1
    }

    override fun messageId(mail: Mail): String {
        // TODO: real one is on the conversation entry
        return "${mail.getId().elementId.asString()}@tutanota.com"
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

    override fun body(bodyId: Id): String {
        return mailsDb.readBody(bodyId.asString()) ?: runBlocking {
            api.loadElementEntity<MailBody>(bodyId)
        }.let {
            mailsDb.writeBody(it)
            it.compressedText ?: it.text ?: ""
        }
    }

    override fun markUnread(mail: Mail, unread: Boolean) {
        val dbMail = mail

        // important! Copy IVs as well, it's not handled by the copy (yet}
        val unreadMail = dbMail.copy(unread = unread)
        unreadMail.finalIvs = dbMail.finalIvs
        runBlocking {
            api.updateEntity(unreadMail)
        }
    }

    override fun getFiles(mail: Mail): List<File> {
        return mail.attachments.mapNotNull { mailsDb.readFile(it) }
    }

    override fun getFileData(file: File): ByteArray {
        return runBlocking {
            fileFacade.downloadFile(file)
        }.data
    }
}