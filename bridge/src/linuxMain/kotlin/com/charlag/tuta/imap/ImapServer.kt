package com.charlag.tuta.imap

import com.charlag.tuta.MailFolderType
import com.charlag.tuta.entities.Date
import com.charlag.tuta.entities.IdTuple
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailFolder
import com.charlag.tuta.toBytes
import com.charlag.tuta.toRFC3501
import com.charlag.tuta.toRFC822
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ImapServer(private val mailLoader: MailLoader) {
    private var currentFolder: MailFolder? = null

    fun newConnection(): List<String> {
        return listOf("* OK IMAP4rev1 Service Ready")
    }

    fun respondTo(message: String): List<String> {
        val messageParts = message.dropLast(2).split(' ', limit = 3)
        val (tag, commandLower) = messageParts
        val command = commandLower.toUpperCase()
        val args = messageParts.getOrElse(2) { "" }

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
                        listOf(
                            """* ${mailLoader.numberOfMailsFor(inbox)} EXISTS""",
                            """* 0 RECENT""",
//                            """* OK [UNSEEN 1] Message 1 is first unseen""",
                            // we user valid UIDs always
                            """* OK [UIDVALIDITY 1] UIDs valid""",
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
                handleFetch(args, tag, command)
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
                if (args.startsWith("FETCH ") || args.startsWith("fetch ")) {
                    val effectiveArgs = args.substring("FETCH ".length)
                    val fetchCommand = this.parseFetch(effectiveArgs)
                    val selectedFolder =
                        currentFolder ?: return listOf("$tag NO INBOX SELECTED")

                    val idParam = fetchCommand.idParam
                    val mails = when (idParam) {
                        is IdParam.Singe -> {
                            val mail = mailLoader.mailByUid(selectedFolder, idParam.id)
                                ?: return listOf("$tag NO EMAIL FOUND")
                            listOf(mail)
                        }
                        is IdParam.OpenRange -> mailLoader.mailsByUid(
                            selectedFolder,
                            idParam.startId,
                            null
                        )
                        is IdParam.ClosedRange -> mailLoader.mailsByUid(
                            selectedFolder,
                            idParam.startId,
                            idParam.endId
                        )
                    }
                    respondToFetch(mails, fetchCommand.attrs) + success(
                        tag,
                        "UID FETCH"
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

    private fun handleFetch(args: String, tag: String, command: String): List<String> {
        val fetchCommand = this.parseFetch(args)
        val idParam = fetchCommand.idParam
        println("fetchCommand $fetchCommand")
        val folder = this.currentFolder ?: return listOf("$tag NO NO FOLDER SELECTED")
        val mails = when (idParam) {
            is IdParam.Singe -> {
                val mail = mailLoader.mailBySeq(folder, idParam.id)
                    ?: return listOf("$tag NO EMAIL FOUND")
                listOf(mail)
            }
            is IdParam.OpenRange -> mailLoader.mailsBySeq(
                folder,
                idParam.startId,
                null
            )
            is IdParam.ClosedRange -> mailLoader.mailsBySeq(
                folder,
                idParam.startId,
                idParam.endId
            )
        }
        return respondToFetch(mails, fetchCommand.attrs) + success(tag, command)
    }

    private fun respondToFetch(mails: List<Mail>, attrs: List<FetchAttr>): List<String> {
        return mails.mapIndexed { i: Int, mail: Mail ->
            val response = StringBuilder("* ${i + 1} FETCH (")

            val parts =
                sequenceOf(FetchPartResponse.Simple("UID", mailLoader.uid(mail).toString())) +
                        (attrs.asSequence()
                            .map { fetchAttr -> fetchPart(fetchAttr, mail) }
                            .filterNotNull())
            val partsResponse = parts.joinToString(" ") { r ->
                when (r) {
                    is FetchPartResponse.Simple -> "${r.name} ${r.value}"
                    is FetchPartResponse.Data -> {
                        val bytesLength = r.value.toBytes().size
                        if (bytesLength != r.value.length) {
                            println("Size differs for ${r.value.take(20)}")
                        }
                        "${r.name} {$bytesLength}\r\n${r.value}"
                    }
                }
            }
            response.append(partsResponse)
            response.append(')')
            response.toString()
        }
    }

    private fun fetchPart(fetchAttr: FetchAttr, mail: Mail): FetchPartResponse? {
        return when (fetchAttr) {
            is FetchAttr.Simple -> when (fetchAttr.value) {
                "FLAGS" -> if (mail.unread) {
                    FetchPartResponse.Simple(fetchAttr.value, "()")
                } else {
                    FetchPartResponse.Simple(fetchAttr.value, "(\\Seen)")
                }
                "INTERNALDATE" -> {
                    val date = mail.receivedDate.toRFC3501()
                    FetchPartResponse.Simple(fetchAttr.value, date)
                }
                // TODO
                "RFC822.SIZE" -> FetchPartResponse.Simple(fetchAttr.value, "9")
                "UID" -> null // ignore, we always append it
                else -> {
                    println("I don't know attr ${fetchAttr.value}")
                    null
                }
            }
            is FetchAttr.Parametrized -> when (fetchAttr.value) {
                "BODY", "BODY.PEEK" -> {
                    val sectionSpec = fetchAttr.sectionSpec
                        ?: error("I don't fetch lists without spec yet!")
                    return when (sectionSpec.section) {
                        // All headers
                        "HEADER" -> {
                            val headers = makeHeaders(mail, headerNames)
                            FetchPartResponse.Data("BODY[HEADER]", headers)
                        }
                        // Some headers
                        "HEADER.FIELDS" -> {
                            val headers = makeHeaders(mail, sectionSpec.fields)
                            val fields = sectionSpec.fields.joinToString(" ")
                            FetchPartResponse.Data("BODY[HEADER.FIELDS ($fields)]", headers)
                        }
                        // The whole body
                        null -> {
                            val body = this.mailLoader.body(mail).let {
                                it.compressedText ?: it.text
                            }!!
                            val headers = makeHeaders(
                                mail,
                                headerNames
                            )
                            FetchPartResponse.Data("BODY[]", headers + body)
                        }
                        else -> {
                            println("I don't know $fetchAttr")
                            null
                        }
                    }
                }
                else -> {
                    println("I don't konw $fetchAttr")
                    null
                }
            }
        }
    }

    private val headerNames = setOf(
        "FROM",
        "TO",
        "CC",
        "BCC",
        "REPLY-TO",
        "DATE",
        "CONTENT-TYPE",
        "SUBJECT",
        "MESSAGE-ID",
        "IN-REPLY-TO"
    )

    private sealed class FetchPartResponse {
        data class Simple(val name: String, val value: String) : FetchPartResponse()
        data class Data(val name: String, val value: String) : FetchPartResponse()
    }

    private fun makeHeaders(mail: Mail, headerNames: Collection<String>): String {
        val headers = StringBuilder()
        for (field in headerNames) {
            makeUpHeader(mail, field)?.let {
                headers.append(it)
                headers.append("\r\n")
            }
        }
        // >Subsetting does not exclude the [RFC-2822] delimiting blank line between the header and
        // >the body; the blank line is included in all header fetches, except in the case of
        // >a message which has no body and no blank line.
        //
        // https://tools.ietf.org/html/rfc3501#section-6.4.5
        //
        // It seems like some mails clients (like mutt) *really* want this newlineq.
        return headers.append("\r\n").toString()
    }

    private fun makeUpHeader(mail: Mail, header: String): String? {
        val normalizedHeader = header.toUpperCase()
        if (header in neverSupportedHeadres || header in temporarilyNotAvailableHeaders) {
            return null
        }
        return when (normalizedHeader) {
            "TO" -> {
                val r = mail.toRecipients.firstOrNull() ?: return null
                return "To: ${r.address}"
            }
            "CC" -> {
                val cc = mail.ccRecipients.firstOrNull() ?: return null
                return "Cc: ${cc.address}"
            }
            "BCC" -> {
                val bcc = mail.bccRecipients.firstOrNull() ?: return null
                return "Bcc: ${bcc.address}"
            }
            "REPLY-TO" -> {
                val replyTo = mail.replyTos.firstOrNull() ?: return null
                return "Reply-To: ${replyTo.address}"
            }
            "FROM" -> return "From: ${mail.sender.address}"
            "SUBJECT" -> return "Subject: ${mail.subject}"
            "MESSAGE-ID" -> return "Message-Id: <${mail.getId().elementId.asString()}@tutanota.com>"
            "DATE" -> {
                val date = mail.receivedDate.toRFC822()
                return "Date: $date"
            }
            // TODO
            "CONTENT-TYPE" -> return "Content-Type: text/html; charset=utf-8"
            else -> {
                println("Unknown header: $header")
                return null
            }
        }
    }

    fun success(tag: String, command: String) = "$tag OK $command COMPLETED"


    private val parseFetch: (String) -> FetchRequest = fetchCommandParser().build()

}

private val neverSupportedHeadres = setOf("Priority", "X-Priority", "Newsgroups")
private val temporarilyNotAvailableHeaders = setOf("References", "In-Reply-To")

private fun Date.toInstant() = Instant.fromEpochMilliseconds(this.millis)

private fun Date.toRFC822(): String = this.toInstant()
    .toLocalDateTime(TimeZone.currentSystemDefault())
    .toRFC822()

private fun Date.toRFC3501(): String =
    this.toInstant().toLocalDateTime(TimeZone.currentSystemDefault())
        .toRFC3501()

fun Mail.getId(): IdTuple = this._id ?: error("No id! $this")