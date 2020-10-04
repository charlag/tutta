package com.charlag.tuta.imap

import com.charlag.tuta.entities.IdTuple
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailBody
import com.charlag.tuta.entities.tutanota.MailFolder

interface MailLoader {
    fun mail(id: IdTuple): Mail

    fun mails(folder: MailFolder): List<Mail>

    fun folders(): List<MailFolder>

    fun body(mail: Mail): MailBody
}