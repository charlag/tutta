package com.charlag.tuta.imap

import com.charlag.tuta.*
import com.charlag.tuta.entities.Date
import com.charlag.tuta.entities.IdTuple
import com.charlag.tuta.entities.tutanota.EncryptedMailAddress
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailAddress
import com.charlag.tuta.entities.tutanota.MailFolder
import deriveHackyUid
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import kotlin.math.min

private data class ReadingState(val tag: String, var toRead: Int)

class ImapServer(private val mailLoader: MailLoader, private val syncHandler: SyncHandler) {
    private var currentFolder: MailFolder? = null
    private var readingState: ReadingState? = null

    fun newConnection(): List<String> {
        return listOf("* OK IMAP4rev1 Service Ready")
    }

    fun respondTo(message: String): List<String> {
        val readingState = this.readingState
        if (readingState != null) {
            val size = message.toBytes().size
            val isEmpty = message == ""
            // After data is over, there's an empty line
            if (readingState.toRead > 0) {
                readingState.toRead -= size
                return listOf()
            } else if (isEmpty && readingState.toRead == 0) {
                this.readingState = null
                return listOf(success(readingState.tag, "APPEND"))
            } else {
                return listOf("* BAD")
            }
        }

        return message.split("\r\n").flatMap { line ->
            // If we are not reading body then empty line shouldn't matter to us
            if (line.isEmpty()) return@flatMap emptyList()

            val messageParts = line.split(' ', limit = 3)
            if (messageParts.size < 2) {
                return listOf("* BAD")
            }
            val (tag, commandLower) = messageParts
            val command = commandLower.toUpperCase()
            val args = messageParts.getOrElse(2) { "" }
            respondToCommand(tag, command, args)
        }
    }

    private fun respondToCommand(tag: String, command: String, args: String): List<String> {
        return when (command) {
            "CAPABILITY" -> {
                listOf("* CAPABILITY IMAP4rev1", success(tag, command))
            }
            "LOGIN" -> {
                listOf(success(tag, command))
            }
            "LIST", "LSUB" -> {
                // In some cases should return hierarchy delimeter
                val (delimiter, pattern) = this.parseList(args)
                if (delimiter == "") {
                    if (pattern == "") {
                        //      An empty ("" string) mailbox name argument is a special request to
                        //      return the hierarchy delimiter and the root name of the name given
                        //      in the reference.  The value returned as the root MAY be the empty
                        //      string if the reference is non-rooted or is an empty string.  In
                        //      all cases, a hierarchy delimiter (or NIL if there is no hierarchy)
                        //      is returned.  This permits a client to get the hierarchy delimiter
                        //      (or find out that the mailbox names are flat) even when no
                        //      mailboxes by that name currently exist.
                        //
                        //      https://tools.ietf.org/html/rfc3501#section-6.3.8
                        listOf("""* LIST (\Noselect) "/" """"", success(tag, command))
                    } else {
                        val folders = mailLoader.folders()
                        val folderResponses = folders.mapNotNull { folder ->
                            val name = folder.imapName
                            if (pattern == "*" ||
                                pattern == "%" || // should not match subfolders
                                name.contains(pattern, ignoreCase = true)
                            ) {
                                val flags = mutableListOf("\\HasNoChildren")
                                folder.specialUse?.let { flags.add(it) }
                                "* LIST (${flags.joinToString(" ")}) \"/\" \"$name\""
                            } else {
                                null
                            }
                        }
                        folderResponses + success(tag, command)
                    }
                } else {
                    listOf(success(tag, command))
                }
            }
            "SELECT", "EXAMINE" -> {
                // SELECT and EXAMINE do the same thing - show info about mailbox - but EXAMINE does
                // not reset RECENT flag nor does any other changes
                if (args.isEmpty()) {
                    return listOf("${tag} BAD NO ARGS FOR SELECT")
                }
                val folder = this.getFolderByImapName(args) ?: return listOf("$tag NO such folder")
                this.currentFolder = folder
                val nextUid = mailLoader.nextUid(folder)
                listOf(
                    """* ${mailLoader.numberOfMailsFor(folder)} EXISTS""",
                    """* 0 RECENT""",
//                            """* OK [UNSEEN 1] Message 1 is first unseen""",
                    // we user valid UIDs always
                    """* OK [UIDVALIDITY 1] UIDs valid""",
                    """* OK [UIDNEXT ${nextUid}] Predicted next UID""",
                    """* FLAGS (\Answered \Flagged \Deleted \Seen)""",
                    """* OK [PERMANENTFLAGS (\Deleted \Seen \*)] Limited""",
                    """$tag OK [READ-WRITE] $command completed""",
                )
            }
            "NOOP" -> listOf(success(tag, command))
            "FETCH" -> {
                handleFetch(args, tag, command)
            }
            "CREATE" -> listOf(success(tag, command))
            "UID" -> {
                // TODO: subcommand parsing is a mess, leave it to the parser

                // for UID FETCH and UID SEARCH, same as normal commands but with UID instead of
                // sequence numbers
                if (args.startsWith("FETCH ", ignoreCase = true)) {
                    val effectiveArgs = args.substring("FETCH ".length)
                    val fetchCommand = this.parseFetch(effectiveArgs)
                    val selectedFolder =
                        currentFolder ?: return listOf("$tag NO INBOX SELECTED")

                    val idParam = fetchCommand.idParam
                    val mails = when (idParam) {
                        is IdParam.IdSet -> {
                            idParam.ids.map {
                                mailLoader.mailByUid(selectedFolder, it)
                                    ?: return listOf("$tag NO EMAIL FOUND")
                            }
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
                } else if (args.startsWith("search ", ignoreCase = true)) {
                    val effectiveArgs = args.substring("search ".length)
                    val searchCommand = searchCommandParser.build()(effectiveArgs)
                    if (searchCommand.size == 1 && searchCommand[0] is SearchCriteria.Since) {
                        val criteria: SearchCriteria.Since =
                            searchCommand[0] as SearchCriteria.Since
                        val folder = this.currentFolder ?: return listOf("$tag NO")
                        val date =
                            LocalDate(criteria.date.year, criteria.date.month, criteria.date.day)
                        val uid = date.atStartOfDayIn(TimeZone.currentSystemDefault())
                            .epochSeconds.toInt()
                        val mails = this.mailLoader.mailsByUid(folder, uid, null)
                        val uids =
                            mails.joinToString(separator = " ") { it.deriveHackyUid().toString() }
                        listOf(
                            "* SEARCH $uids",
                            success(tag, "search"),
                        )
                    } else {
                        listOf("$tag NO")
                    }
                } else if (args.startsWith("store ", ignoreCase = true)) {
                    val effectiveArgs = args.substring("store ".length)
                    val storeCommand = storeCommandParser.build()(effectiveArgs)
                    val selectedFolder =
                        currentFolder ?: return listOf("$tag NO INBOX SELECTED")
                    if (storeCommand.flags.size != 1 ||
                        !storeCommand.flags[0].equals("\\seen", ignoreCase = true)
                    ) {
                        return listOf("$tag NO not allowed to change flags")
                    }
                    val id = storeCommand.id
                    val mails = when (id) {
                        is IdParam.IdSet ->
                            id.ids.mapNotNull { mailLoader.mailByUid(selectedFolder, it) }
                        is IdParam.ClosedRange ->
                            mailLoader.mailsByUid(selectedFolder, id.startId, id.endId)
                        is IdParam.OpenRange ->
                            return listOf("$tag NO not allowed to store with open range")
                    }
                    val unread = storeCommand.operation == FlagOperation.REMOVE
                    val unreadFlag = if (unread) "" else "\\SEEN"
                    val responses = mails.mapIndexed { index, mail ->
                        mailLoader.markUnread(mail, unread)
                        // TODO: more flags?
                        "* ${index + 1} FETCH (UID ${mail.deriveHackyUid()} FLAGS (${unreadFlag}))"
                    }
                    return responses + success(tag, "store")
                } else {
                    listOf("$tag BAD $command $args")
                }
            }
            "LOGOUT" -> listOf(success(tag, command))
            "STATUS" -> {
                val statusCommand = parseStatus(args)
                val folder = getFolderByImapName(statusCommand.folder)
                    ?: return listOf("$tag NO ${statusCommand.folder}")
                val attrs = statusCommand.attributes.mapNotNull { attr ->
                    val value = when (attr) {
                        "MESSAGES" -> this.mailLoader.numberOfMailsFor(folder)
                        "UNSEEN" -> this.mailLoader.numberOfMailsFor(folder)
                        // Placeholder
                        "RECENT" -> 0
                        "UIDNEXT" -> return@mapNotNull null
                        else -> return@mapNotNull null
                    }
                    "$attr $value"
                }
                listOf(
                    "* STATUS ${statusCommand.folder} (${attrs.joinToString(" ")})",
                    success(tag, command)
                )
            }
            // TODO
            "APPEND" -> {
                val appendCommand = appendParser.build()(args)
                this.readingState = ReadingState(tag, appendCommand.literalSize)
                listOf("+")
            }
            "CHECK" -> {
                runBlocking {
                    syncHandler.resync()
                }
                listOf(success(tag, command))
            }
            "CLOSE" -> {
                // Noop for now
                listOf(success(tag, command))
            }
            else -> {
                println("# unknown command '$command'")
                listOf("$tag BAD IDK WHAT THIS MEANS $command")
            }
        }
    }

    private fun getFolderByImapName(name: String): MailFolder? {
        val canonicalName = name.trim(' ').toUpperCase()
        return this.mailLoader.folders().find { it.imapName == canonicalName }
    }

    private fun handleFetch(args: String, tag: String, command: String): List<String> {
        val fetchCommand = this.parseFetch(args)
        val idParam = fetchCommand.idParam
        println("fetchCommand $fetchCommand")
        val folder = this.currentFolder ?: return listOf("$tag NO NO FOLDER SELECTED")
        val mails = when (idParam) {
            is IdParam.IdSet -> {
                idParam.ids.map {
                    mailLoader.mailByUid(folder, it)
                        ?: return listOf("$tag NO EMAIL FOUND")
                }
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
                            println("Size differs for ${r.value.take(60)}")
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
            is FetchAttr.Simple -> when (fetchAttr.value.toUpperCase()) {
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
                "RFC822.HEADER" -> {
                    val headers = makeHeaders(mail, headerNames)
                    FetchPartResponse.Data("RFC822.HEADER", headers)
                }
                "UID" -> null // ignore, we always append it
                "ENVELOPE" -> {
                    // The fields of the envelope structure are in the following
                    //         order: date, subject, from, sender, reply-to, to, cc, bcc,
                    //         in-reply-to, and message-id.  The date, subject, in-reply-to,
                    //         and message-id fields are strings.  The from, sender, reply-to,
                    //         to, cc, and bcc fields are parenthesized lists of address
                    //         structures.

                    // ("Wed, 17 Jul 1996 02:23:25 -0700 (PDT)"
                    //      "IMAP4rev1 WG mtg summary and minutes"
                    //      (("Terry Gray" NIL "gray" "cac.washington.edu"))
                    //      (("Terry Gray" NIL "gray" "cac.washington.edu"))
                    //      (("Terry Gray" NIL "gray" "cac.washington.edu"))
                    //      ((NIL NIL "imap" "cac.washington.edu"))
                    //      ((NIL NIL "minutes" "CNRI.Reston.VA.US")
                    //      ("John Klensin" NIL "KLENSIN" "MIT.EDU")) NIL NIL
                    //      "<B27397-0100000@cac.washington.edu>")
                    val response = listOf(
                        mail.receivedDate.toRFC822().quote(),
                        mail.subject.quote(),
                        "(${formatMailStructure(mail.sender)})",
                        "(${
                            formatMailStructure(mail.differentEnvelopeSender?.let {
                                MailAddress(address = it, name = "", contact = null)
                            } ?: mail.sender)
                        })",
                        "(${
                            mail.replyTos.map { formatMailStructure(it.toMailAddress()) }
                                .joinToString(" ")
                        })",
                        "(${mail.toRecipients.map { formatMailStructure(it) }.joinToString(" ")})",
                        "(${mail.ccRecipients.map { formatMailStructure(it) }.joinToString(" ")})",
                        "(${mail.bccRecipients.map { formatMailStructure(it) }.joinToString(" ")})",
                        "NIL", // TODO In-reply-to,
                        "<${mailLoader.messageId(mail)}>".quote()
                    ).joinToString(" ", prefix = "(", postfix = ")")
                    FetchPartResponse.Simple(name = "ENVELOPE", value = response)
                }
                else -> {
                    println("I don't know attr ${fetchAttr.value}")
                    null
                }
            }
            is FetchAttr.Parametrized -> when (fetchAttr.value.toUpperCase()) {
                "BODY", "BODY.PEEK" -> {
                    val sectionSpec = fetchAttr.sectionSpec
                        ?: error("I don't fetch lists without spec yet!")
                    // TODO: handle partial in all of these
                    return when (sectionSpec.section?.toUpperCase()) {
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
                        // Just body
                        "TEXT" -> {
                            bodyPart(mail, fetchAttr.range, "BODY[TEXT]")
                        }
                        // The whole body
                        null -> {
                            val body = this.mailLoader.body(mail)
                            val headers = makeHeaders(
                                mail,
                                headerNames
                            )
                            FetchPartResponse.Data("BODY[]", headers + body)
                        }
                        else -> {
                            // Most of these cases should be handled by the parser instead

                            // must be a part selector like "1.mime" or just "1"
                            val parts = sectionSpec.section.split('.')
                            val (before) = parts
                            val after = parts.getOrNull(1)
                            val partNumber = before.toIntOrNull()
                                ?: throw ParserError("Part number is invalid: ${sectionSpec.section}")
                            // For now only one part
                            return if (partNumber > 1) {
                                return null
                            } else if (after == null) {
                                bodyPart(mail, fetchAttr.range, "BODY[1]")
                            } else if (after.equals("mime", ignoreCase = true)) {
                                FetchPartResponse.Simple("BODY[1.MIME]", "TEXT/HTML")
                            } else {
                                println("I don't know section ${sectionSpec}")
                                null
                            }
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

    private fun bodyPart(
        mail: Mail,
        range: Pair<Int, Int>?,
        name: String,
    ): FetchPartResponse.Data {
        val body = this.mailLoader.body(mail)
        return if (range != null) {
            val partialBody = body.toBytes()
                .copyOfNumber(range.first, range.second)
                .decodeToString()
            FetchPartResponse.Data(
                "$name<${range.first}.${range.second}>",
                partialBody
            )
        } else {
            FetchPartResponse.Data(name, body)
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
        if (header in neverSupportedHeaders || header in temporarilyNotAvailableHeaders) {
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
            "MESSAGE-ID" -> return "Message-Id: <${mailLoader.messageId(mail)}>"
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
    private val parseList: (String) -> ListCommand = listCommandParser.build()
    private val parseStatus: (String) -> StatusCommand = statusParser.build()
}

@SharedImmutable
private val neverSupportedHeaders = setOf("Priority", "X-Priority", "Newsgroups")

@SharedImmutable
private val temporarilyNotAvailableHeaders = setOf("References", "In-Reply-To")

private fun Date.toInstant() = Instant.fromEpochMilliseconds(this.millis)

private fun Date.toRFC822(): String = this.toInstant()
    .toLocalDateTime(TimeZone.currentSystemDefault())
    .toRFC822()

private fun Date.toRFC3501(): String =
    this.toInstant().toLocalDateTime(TimeZone.currentSystemDefault())
        .toRFC3501()

fun Mail.getId(): IdTuple = this._id ?: error("No id! $this")

private val MailFolder.imapName
    get() = when (this.folderType) {
        MailFolderType.INBOX.value -> "INBOX"
        MailFolderType.SENT.value -> "SENT"
        MailFolderType.DRAFT.value -> "DRAFTS"
        MailFolderType.ARCHIVE.value -> "ARCHIVE"
        MailFolderType.SPAM.value -> "JUNK"
        MailFolderType.TRASH.value -> "WASTEBASKET"
        else -> error("Unknown folder type ${this.folderType}")
    }

private val MailFolder.specialUse: String?
    get() = when (this.folderType) {
        MailFolderType.INBOX.value -> null
        MailFolderType.SENT.value -> "\\Sent"
        MailFolderType.DRAFT.value -> "\\Drafts"
        MailFolderType.ARCHIVE.value -> "\\Archive"
        MailFolderType.SPAM.value -> "\\Junk"
        MailFolderType.TRASH.value -> "\\Wastebasket"
        else -> error("Unknown folder type ${this.folderType}")
    }

fun String.surroundWith(s: CharSequence): String = "$s$this$s"

fun String.quote() = replace("\"", "\\").surroundWith("\"")


fun formatMailStructure(mailAddress: MailAddress): String {
    return "(${mailAddress.name.quote()} NIL ${
        mailAddress.address.substringBefore('@').quote()
    } ${mailAddress.address.substringAfter('@').quote()})"
}

fun EncryptedMailAddress.toMailAddress() =
    MailAddress(address = address, name = name, contact = null)


fun ByteArray.copyOfNumber(from: Int, number: Int): ByteArray =
    copyOfRange(min(from, size), min(from + number, size))