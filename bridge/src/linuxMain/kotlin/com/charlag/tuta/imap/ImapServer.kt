package com.charlag.tuta.imap

import com.charlag.tuta.MailFolderType
import com.charlag.tuta.entities.IdTuple
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailFolder
import com.charlag.tuta.toBytes

class ImapServer(private val mailLoader: MailLoader) {
    private var currentFolder: MailFolder? = null

    fun newConnection(): List<String> {
        return listOf("* OK IMAP4rev1 Service Ready")
    }

    fun respondTo(message: String): List<String> {
        val messageParts = message.dropLast(2).split(' ', limit = 3)
        val (tag, command) = messageParts
        val args = messageParts.getOrElse(2) { "" }

        val headers =
            "To: user1@127.0.0.1\r\nFrom: user1 <user1@127.0.0.1>\r\nSubject: Testing to self\r\nMessage-ID: <6c8ab0be-3757-34c3-3e17-8290ed54f6ae@127.0.0.1>\r\nDate: Sat, 3 Oct 2020 19:35:33 +0200\r\nContent-Type: text/plain; charset=utf-8; format=flowed"

        return when (command) {
            "CAPABILITY" -> {
                listOf("* CAPABILITY IMAP4rev1", success(tag, command))
            }
            "LOGIN" -> {
                listOf(success(tag, command))
            }
            "LIST" -> {
                // In some cases should return hierarchy delimeter
                listOf("""* LIST (\Noselect) "/" """"", success(tag, command))
            }
            "SELECT", "EXAMINE" -> {
                // SELECT and EXAMINE do the same thing - show info about mailbox - but EXAMINE does
                // not reset RECENT flag nor does any other changes
                when {
                    args.isEmpty() -> {
                        listOf("* BAD NO ARGS FOR SELECT")
                    }
                    args == "\"INBOX\"" || args == "INBOX" -> {
                        val inbox = this.mailLoader.folders()
                            .first { it.folderType == MailFolderType.INBOX.value }
                        this.currentFolder = inbox
                        val inboxMails = this.mailLoader.mails(inbox)
                        listOf(
                            """* ${inboxMails.size} EXISTS""",
                            """* 0 RECENT""",
//                            """* OK [UNSEEN 1] Message 1 is first unseen""",
                            """* OK [UIDVALIDITY 3857529045] UIDs valid""",
                            """* FLAGS (\Answered \Flagged \Deleted \Seen)""",
                            """* OK [PERMANENTFLAGS (\Deleted \Seen \*)] Limited""",
                            """$tag OK [READ-WRITE] $command completed""",
                        )
                    }
                    else -> {
                        listOf("* BAD IDK HOW TO $tag $args")
                    }
                }
            }
            "NOOP" -> listOf(success(tag, command))
            "FETCH" -> {
                respondToFetch(args, tag, command)
//                // get some email details
//                listOf(
//                    // FETCH 1:1 (UID FLAGS INTERNALDATE RFC822.SIZE BODY.PEEK[HEADER.FIELDS (DATE FROM SENDER SUBJECT TO CC MESSAGE-ID REFERENCES CONTENT-TYPE CONTENT-DESCRIPTION IN-REPLY-TO REPLY-TO LINES LIST-POST X-LABEL)])
//                    """* 1 FETCH (FLAGS (\Seen) INTERNALDATE "17-Jul-1996 02:44:25 -0700" RFC822.SIZE 9 UID 1 BODY[HEADER.FIELDS (DATE FROM SENDER SUBJECT TO CC MESSAGE-ID REFERENCES CONTENT-TYPE CONTENT-DESCRIPTION IN-REPLY-TO REPLY-TO LINES LIST-POST X-LABEL)] {${headers.encodeToByteArray().size + 2}}""",
//                    headers,
//                    ")",
//                    success(tag, command)
//                )
            }
            "UID" -> {
                // for UID FETCH and UID SEARCH, same as normal commands but with UID instead of
                // sequence numbers
                if (args == "FETCH") {
                    val body = "Hello this is a TEST"
                    listOf(
                        "* 1 FETCH (UID 1 BODY[] {${headers.toBytes().size + 2 + body.toBytes().size + 2}}",
                        headers,
                        body,
                        ")",
                        success(tag, "FETCH")
                    )
                } else {
                    listOf("$tag BAD $command $args")
                }
            }
            "LOGOUT" -> listOf(success(tag, command))
            else -> {
                println("# unknown command '$command'")
                listOf("$tag BAD IDK WHAT THIS MEANS $command")
            }
        }
    }

    private fun respondToFetch(args: String, tag: String, command: String): List<String> {
        val fetchCommand = this.fetchParser(args)
        val idParam = fetchCommand.idParam
        println("fetchCommand $fetchCommand")
        val folder = this.currentFolder ?: return listOf("$tag NO NO FOLDER SELECTED")
        val toFetch = when (idParam) {
            is IdParam.Singe -> idParam.id..idParam.id
            is IdParam.Range -> idParam.startId..idParam.endId
        }
        val mails = this.mailLoader.mails(folder)
        // TODO: rewrite most of the following
        val responses: List<String> = mails.mapIndexed { i: Int, mail: Mail ->
            val response = StringBuilder("* ${i + 1} FETCH (")
            for (fetchAttr in fetchCommand.field) {
                when (fetchAttr) {
                    is FetchAttr.Simple -> when (fetchAttr.value) {
                        "FLAGS" -> response.append(if (mail.unread) " FLAGS ()" else " FLAGS (\\Seen)")
                        // TODO
                        "INTERNALDATE" -> response.append(" INTERNALDATE \"17-Jul-1996 02:44:25 -0700\"")
                        // TODO
                        "RFC822.SIZE" -> response.append(" RFC822.SIZE 9")
                        "UID" -> response.append(" UID ${mail.getId().elementId.asString()}")
                        else -> println("I don't know attr ${fetchAttr.value}")
                    }
                    is FetchAttr.Parametrized -> when (fetchAttr.value) {
                        "BODY", "BODY.PEEK" -> {
                            val section = fetchAttr.sectionSpec.section
                            if (section != "HEADER.FIELDS") {
                                println("I don't know section $section")
                            } else {
                                val headers = StringBuilder()
                                for (field in fetchAttr.sectionSpec.fields) {
                                    when (field) {
                                        "TO" -> {
                                            val r = mail.toRecipients.firstOrNull() ?: continue
                                            headers.append("To: ${r.address}\r\n")
                                        }
                                        "FROM" -> headers.append("From: ${mail.sender.address}\r\n")
                                        "SUBJECT" -> headers.append("Subject: ${mail.subject}\r\n")
                                        "MESSAGE-ID" -> headers.append("Message-Id: <${mail.getId().elementId.asString()}@tutanota.com>\r\n")
                                        // TODO
                                        "DATE" -> headers.append("Date: \"17-Jul-1996 02:44:25 -0700\"\r\n")
                                        // TODO
                                        "CONTENT-TYPE" -> headers.append("Content-Type: text/plain; charset=utf-8; format=flowed\r\n")
                                    }
                                }
                                val length = headers.toString().toBytes().size
                                val fields = fetchAttr.sectionSpec.fields.joinToString(" ")
                                response.append(" BODY[${section} (${fields})] {${length}}\r\n")
                                response.append(headers)
                            }
                        }
                    }
                }
            }
            response.append(')')
            response.toString()
        }
        return responses + success(tag, command)
    }

    fun success(tag: String, command: String) = "$tag OK $command COMPLETED"


    private val fetchParser = fetchCommandParser().asFunction()
}


private fun Mail.getId(): IdTuple = this._id ?: error("No id! $this")