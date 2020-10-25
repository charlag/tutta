package com.charlag.tuta.imap.commands

import com.charlag.mailutil.*
import com.charlag.tuta.*
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailAddress
import com.charlag.tuta.entities.tutanota.MailFolder
import com.charlag.tuta.imap.*

class FetchHandler(private val mailLoader: MailLoader) {
    fun handleUidFetch(currentFolder: MailFolder, args: String, tag: String): List<String> {
        val effectiveArgs = args.substring("FETCH ".length)
        val fetchCommand = this.parseFetch(effectiveArgs)

        val ids = fetchCommand.ids
        val mails = mailLoader.loadMailsByUidParam(currentFolder, ids)
        return respondToFetch(mails, fetchCommand.attrs) + successResponse(
            tag,
            "UID FETCH"
        )
    }

    fun handleFetch(
        currentFolder: MailFolder,
        args: String,
        tag: String,
        command: String
    ): List<String> {
        val fetchCommand = this.parseFetch(args)
        val mails = fetchCommand.ids.flatMap { id ->
            when (id) {
                is IdParam.Id -> listOfNotNull(mailLoader.mailBySeq(currentFolder, id.id))
                is IdParam.EndOpenRange -> mailLoader.mailsBySeq(
                    currentFolder,
                    id.startId,
                    null,
                )
                is IdParam.StartOpenRange -> mailLoader.mailsBySeq(currentFolder, 1, id.endId)
                is IdParam.ClosedRange -> mailLoader.mailsBySeq(
                    currentFolder,
                    id.startId,
                    id.endId,
                )
            }
        }
        return respondToFetch(mails, fetchCommand.attrs) + successResponse(tag, command)
    }

    private fun respondToFetch(mails: List<MailWithUid>, attrs: List<FetchAttr>): List<String> {
        return mails.mapIndexed { i: Int, mail: MailWithUid ->
            val response = StringBuilder("* ${i + 1} FETCH (")

            val parts =
                sequenceOf(FetchPartResponse.Simple("UID", mail.uid.toString())) +
                        (attrs.asSequence()
                            .map { fetchAttr -> fetchPart(fetchAttr, mail.mail) }
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
                            mail.replyTos.joinToString(" ") {
                                formatMailStructure(it.toMailAddress())
                            }
                        })",
                        "(${mail.toRecipients.joinToString(" ") { formatMailStructure(it) }})",
                        "(${mail.ccRecipients.joinToString(" ") { formatMailStructure(it) }})",
                        "(${mail.bccRecipients.joinToString(" ") { formatMailStructure(it) }})",
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
                    val (_, sectionSpec, range) = fetchAttr
                    sectionSpec ?: error("I don't fetch lists without spec yet!")
                    // TODO: handle partial in all of these
                    return when (val section = sectionSpec.section?.toUpperCase()) {
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
                            bodyTextResponse(mail, range, "BODY[TEXT]")
                        }
                        // The whole body
                        null -> {
                            // We get the text of the root part but not headers because email
                            // headers and first part headers are kinda the same thing. It's a mess.
                            val body = mailBodyText(mail, range = null)
                            val headers = makeHeaders(
                                mail,
                                headerNames
                            )
                            FetchPartResponse.Data("BODY[]", headers + body)
                        }
                        else -> {
                            // must be a part selector like "1.mime", "1.2.TEXT" or just "1"
                            val mailParts = mailToParts(mail, mailLoader.getFiles(mail))
                            val path = section.split(".")
                            return traversePartPath(mailParts, range, path).let { data ->
                                val rangeString =
                                    if (range != null) "<${range.first}.${range.second}>" else ""
                                val name = "BODY[${sectionSpec.section}]${rangeString}"
                                when (data) {
                                    is TraverseResult.Success ->
                                        FetchPartResponse.Data(name, data.data)
                                    is TraverseResult.NotAllowed -> {
                                        println("Traverse not allowed: ${data.message}")
                                        null
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {
                    println("I don't know $fetchAttr")
                    null
                }
            }
        }
    }

    private fun traversePartPath(
        rootParts: List<Part>,
        range: FetchRange?,
        path: List<String>
    ): TraverseResult {
        //a011 uid fetch 1 body.peek[header.fields (references)]
        //a012 uid fetch 1 body.peek[1]<0.256>
        //a013 uid fetch 1 body.peek[1.mime]
        val (first, rest) = path.headTail()
        val partNumber = first?.toIntOrNull()
            ?: throw ParserError("Part number is invalid $first")
        // path is indexed from 1
        return rootParts.getOrNull(partNumber - 1)?.traverse(range, rest)
            ?: TraverseResult.NotAllowed("No child at $partNumber")
    }

    private tailrec fun Part.traverse(range: FetchRange?, path: List<String>): TraverseResult {
        val (step, rest) = path.headTail()

        return if (rest.isEmpty()) {
            when (step?.toLowerCase()) {
                "mime" -> TraverseResult.Success(partHeader(this))
                "header" -> TraverseResult.Success(partHeader(this))
                "text" -> TraverseResult.Success(partText(this, range))
                null -> TraverseResult.Success(fullPartData(this))
                else -> TraverseResult.NotAllowed("unknown $step")
            }
        } else {
            val partNumber = step?.toIntOrNull()
                ?: throw ParserError("Part number is invalid $step")
            val child = this.childAt(partNumber - 1)
                ?: return TraverseResult.NotAllowed("No child at $partNumber")
            child.traverse(range, rest)
        }
    }

    /**
     * Full part content, headers and text.
     */
    private fun fullPartData(part: Part): String {
        return partHeader(part) + partText(part, null)
    }

    private fun partHeader(part: Part): String {
        val headers = mutableMapOf<String, String>()
        if (part is Part.AttachmentPart) {
            headers["Content-Type"] = part.mime + "; name=" + part.file.name
            headers["Content-Transfer-Encoding"] = "base64"
            headers["Content-Disposition"] = "attachment; filename=${part.file.name}"
        } else {
            headers["Content-Type"] = part.mime
        }
        return headers.entries.joinToString("\r\n", postfix = "\r\n\r\n") { (k, v) -> "$k: $v" }
    }

    /**
     * Only part text, without headers
     */
    private fun partText(part: Part, range: FetchRange?): String {
        val fullData = when (part) {
            is Part.AttachmentPart -> mailLoader.getFileData(part.file)
                .toBase64()
                .chunked(76)
                .joinToString("\r\n")
            is Part.TextHtml -> mailLoader.body(part.bodyId)
            is Part.Multipart ->
                // Who requests partial multipart? It can only be a preview and we can just give
                // them the first part.
                if (range == null) multipartText(part)
                else partText(part.parts.first(), range)
        }
        return if (range != null) {
            fullData.toBytes()
                .copyOfNumber(range.first, range.second)
                .decodeToString()
        } else {
            fullData
        }
    }

    private fun bodyTextResponse(
        mail: Mail,
        range: FetchRange?,
        name: String,
    ): FetchPartResponse.Data {
        val body = mailBodyText(mail, range)
        return if (range != null) {
            FetchPartResponse.Data(
                "$name<${range.first}.${range.second}>",
                body
            )
        } else {
            FetchPartResponse.Data(name, body)
        }
    }

    private fun mailBodyText(mail: Mail, range: FetchRange?): String {
        val mailParts = mailToParts(mail, mailLoader.getFiles(mail))
        println("mailParts $mailParts")
        return partText(mailParts.first(), range)
    }

    private fun multipartText(part: Part.Multipart): String {
        return part.parts.joinToString(
            prefix = partSeparator + "\r\n",
            separator = "\r\n--${partSeparator}\r\n",
            postfix = "\r\n--${partSeparator}--\r\n",
            transform = this::fullPartData,
        )
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
            "CONTENT-TYPE" -> if (mail.attachments.isEmpty()) {
                "Content-Type: text/html; charset=utf-8"
            } else {
                "Content-Type: multipart/mixed; boundary=\"${partSeparator}\""
            }
            else -> {
                println("Unknown header: $header")
                return null
            }
        }
    }

    sealed class TraverseResult {
        data class Success(val data: String) : TraverseResult()
        data class NotAllowed(val message: String) : TraverseResult()
    }

    private sealed class FetchPartResponse {
        data class Simple(val name: String, val value: String) : FetchPartResponse()
        data class Data(val name: String, val value: String) : FetchPartResponse()
    }

    private val parseFetch: (String) -> FetchCommand = fetchCommandParser.build()

    companion object {
        private const val partSeparator = "---------=_MultipartSeparator"
        private val neverSupportedHeaders = setOf("Priority", "X-Priority", "Newsgroups")
        private val temporarilyNotAvailableHeaders = setOf("References", "In-Reply-To")
    }
}


fun formatMailStructure(mailAddress: MailAddress): String {
    return "(${mailAddress.name.quote()} NIL ${
        mailAddress.address.substringBefore('@').quote()
    } ${mailAddress.address.substringAfter('@').quote()})"
}