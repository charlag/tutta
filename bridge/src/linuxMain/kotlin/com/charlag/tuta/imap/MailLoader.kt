package com.charlag.tuta.imap

import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.IdTuple
import com.charlag.tuta.entities.tutanota.File
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailBody
import com.charlag.tuta.entities.tutanota.MailFolder

interface MailLoader {
    fun numberOfMailsFor(folder: MailFolder): Int
    fun numberOfUneadFor(folder: MailFolder): Int
    fun nextUid(folder: MailFolder): Int
    fun messageId(mail: Mail): String
    fun folders(): List<MailFolder>
    fun mailBySeq(folder: MailFolder, seq: Int): MailWithUid?
    fun mailByUid(folder: MailFolder, uid: Int): Mail?
    fun mailsBySeq(folder: MailFolder, start: Int, end: Int?): List<MailWithUid>
    fun mailsByUid(folder: MailFolder, startUid: Int?, endUid: Int?): List<MailWithUid>
    fun body(bodyId: Id): String
    fun markUnread(mail: Mail, unread: Boolean)
    fun getFiles(mail: Mail): List<File>
    fun getFileData(file: File): ByteArray
    fun moveMails(mails: List<Mail>, folder: MailFolder)
}