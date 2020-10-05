package com.charlag.tuta.imap

import com.charlag.tuta.MailFolderType
import com.charlag.tuta.entities.Date
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.IdTuple
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailAddress
import com.charlag.tuta.entities.tutanota.MailBody
import com.charlag.tuta.entities.tutanota.MailFolder
import platform.posix.time
import kotlin.time.seconds

// TODO: rewrite?
//class FakeMailLoader : MailLoader {
//    override fun mails(folder: MailFolder): List<Mail> {
//        return listOf(
//            mail(IdTuple(GeneratedId("listId"), GeneratedId("1")))
//        )
//    }
//
//    override fun mail(id: IdTuple): Mail {
//        return Mail(
//            _id = id,
//            attachments = listOf(),
//            authStatus = 0,
//            confidential = true,
//            toRecipients = listOf(
//                MailAddress(
//                    address = "test@example.com",
//                    name = "Tester",
//                    contact = null
//                )
//            ),
//            ccRecipients = listOf(),
//            sender = MailAddress(
//                address = "sender@tutanota.com",
//                name = "Sendername",
//                contact = null
//            ),
//            bccRecipients = listOf(),
//
//            subject = "Test subject",
//            replyTos = listOf(),
//            body = GeneratedId("3"),
//            receivedDate = Date(time(null).seconds.toLongMilliseconds()),
//            conversationEntry = IdTuple(GeneratedId("1"), GeneratedId("2")),
//            differentEnvelopeSender = null,
//            headers = null,
//            listUnsubscribe = false,
//            movedTime = null,
//            phishingStatus = 0,
//            replyType = 0,
//            restrictions = null,
//            sentDate = Date(0),
//            state = 0,
//            unread = false,
//            trashed = false,
//        )
//    }
//
//    override fun folders(): List<MailFolder> {
//        val folder = MailFolder(
//            mails = GeneratedId("1"),
//            folderType = MailFolderType.INBOX.value,
//            name = "Inbox",
//            parentFolder = null,
//            subFolders = GeneratedId("2"),
//        )
//        return listOf(folder)
//    }
//
//    override fun body(mail: Mail): MailBody {
//        TODO("Not yet implemented")
//    }
//
//}