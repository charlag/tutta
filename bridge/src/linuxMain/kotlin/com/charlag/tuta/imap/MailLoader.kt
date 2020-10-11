package com.charlag.tuta.imap

import com.charlag.tuta.entities.IdTuple
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailBody
import com.charlag.tuta.entities.tutanota.MailFolder

interface MailLoader {
    fun uid(mail: Mail): Int?
    fun numberOfMailsFor(folder: MailFolder): Int
    fun numberOfUneadFor(folder: MailFolder): Int
    fun nextUid(folder: MailFolder): Int
    fun messageId(mail: Mail): String
    fun folders(): List<MailFolder>
    fun mailBySeq(folder: MailFolder, seq: Int): Mail?
    fun mailByUid(folder: MailFolder, uid: Int): Mail?
    fun mailsBySeq(folder: MailFolder, start: Int, end: Int?): List<Mail>
    fun mailsByUid(folder: MailFolder, startUid: Int, endUid: Int?): List<Mail>
    fun body(mail: Mail): String
}