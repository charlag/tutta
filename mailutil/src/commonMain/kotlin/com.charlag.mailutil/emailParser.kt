package com.charlag.mailutil

import com.charlag.mailutil.KnownMimeType.MULTIPART_ALTERNATIVE
import com.charlag.mailutil.KnownMimeType.MULTIPART_MIXED
import com.charlag.mailutil.KnownMimeType.TEXT_HTML
import com.charlag.mailutil.KnownMimeType.TEXT_PLAIN

typealias Headers = Map<String, String>

class ParsedAttachment(val name: String, val mimeType: String, val data: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ParsedAttachment

        if (name != other.name) return false
        if (mimeType != other.mimeType) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "ParsedAttachment(name='$name', mimeType='$mimeType', data=${data.size})"
    }


}

data class ParsedBody(val text: String, val parsedAttachments: List<ParsedAttachment>)

data class ParsedEmail(
    val headers: Headers,
    val parsedBody: ParsedBody,
)

data class MailAddress(val name: String, val address: String)

object KnownMimeType {
    const val TEXT_PLAIN = "text/plain"
    const val TEXT_HTML = "text/html"
    const val MULTIPART_ALTERNATIVE = "multipart/alternative"
    const val MULTIPART_MIXED = "multipart/mixed"
}

enum class ContentTransferEncoding(val rawName: String) {
    SEVEN_BIT("7bit"),
    QUOTED_PRINTABLE("quoted-printable"),
    BASE64("base64"),
    EIGHT_BIT("8bit"),
    BINARY("binary");

    companion object {
        fun fromRaw(rawName: String): ContentTransferEncoding? {
            for (v in values()) {
                if (v.rawName == rawName) {
                    return v
                }
            }
            return null
        }
    }
}

data class MimeType(val type: String, val subtype: String, val parameters: Map<String, String>) {
    val fullType: String get() = "$type/$subtype"
}

fun parseEmail(text: String): ParsedEmail = EmailParser.parseEmail(text)
fun parseMailAddress(text: String): MailAddress = EmailParser.parseMailAddress(text)

/**
 * Holder for parser instances etc. They could be global but initialization order is still broken
 * for globals.
 */
private object EmailParser {
    fun parseEmail(text: String): ParsedEmail {
        val (headerPart, bodyPart) = text.split("\r\n\r\n", limit = 2)
            .also { if (it.size != 2) throw ParserError("Invalid mail, no empty line") }

        val headers = parseHeaders(headerPart)
        val body = parseBody(headers, bodyPart)
        return ParsedEmail(headers, body)
    }

    val parseMailAddress: (String) -> MailAddress =
        (zeroOrMoreParser(characterNotParser('<')).map { it.joinToString("") } +
                characterParser('<').throwAway() +
                oneOrMoreParser(characterNotParser('>')).map { it.joinToString("") } +
                characterParser('>').throwAway()).map { (name, address) ->
            MailAddress(
                name = name,
                address = address,
            )
        }.build()

    private val parseMimeType: (String) -> MimeType = run {
        val allowedSpecialChars =
            listOf('!', '#', '$', '%', '&', '\'', '*', '+', '-', '.', '^', '_', '`', '|', '~')
        val tokenParser = oneOrMoreParser(
            characterInRangeParser('a'..'z') or
                    characterInRangeParser('A'..'Z') or
                    oneOfCharactersParser(digits()) or
                    oneOfCharactersParser(allowedSpecialChars)
        ).map { it.joinToString("") }
        val parameterParser =
            tokenParser + characterParser('=').throwAway() + (quotedStringParser or tokenParser)
        val owsParser = (characterParser(' ') or characterParser('\t')).optional().throwAway()
        (tokenParser + characterParser('/').throwAway() + tokenParser +
                zeroOrMoreParser(
                    owsParser + characterParser(';').throwAway() + owsParser + parameterParser
                ).map { it.toMap() })
            .flatten()
            .map { (type, subtype, params) -> MimeType(type, subtype, params) }
            .build()
    }

    private fun parseHeaders(text: String): Headers {
        val buffer = StringBuilder(text)

        while (true) {
            val index = buffer.indexOf("\r\n ")
            if (index == -1) break
            buffer.deleteRange(index, index + 2)
        }
        val result = mutableMapOf<String, String>()
        val foldedHeaders = mutableListOf<String>()
        for (line in buffer.split("\r\n")) {
            if (line.startsWith(' ') || line.startsWith('\t')) {
                // Add to the last header
                foldedHeaders[foldedHeaders.lastIndex] += line.trimStart()
            } else {
                foldedHeaders.add(line)
            }
        }

        for (line in foldedHeaders) {
            val (name, value) = line.split(": ", limit = 2)
                .also { if (it.size != 2) throw ParserError("Invalid header line: $line") }
            result[name] = value
        }
        return result
    }

    private fun parseBody(headers: Headers, text: String): ParsedBody {
        val mimeType = headers["Content-Type"]?.let { parseMimeType(it) }
            ?: MimeType("text", "plain", mapOf())

        return when (mimeType.fullType) {
            TEXT_PLAIN -> ParsedBody("<pre>$text</pre>", listOf())
            TEXT_HTML -> ParsedBody(text, listOf())
            MULTIPART_ALTERNATIVE -> {
                val selectedText = processAlternativePart(mimeType, text)
                ParsedBody(selectedText, listOf())
            }
            MULTIPART_MIXED -> processMultipartMixed(mimeType, text)
            else -> error("Cannot parse body of type $mimeType")
        }
    }

    private fun processMultipartMixed(
        mimeType: MimeType,
        text: String
    ): ParsedBody {
        val boundary =
            mimeType.parameters["boundary"] ?: error("No boundary for ${mimeType.fullType}")
        val parts = parseMultipartAlternativePart(text, boundary)
        var body: String? = null
        val attachments = mutableListOf<ParsedAttachment>()
        for (part in parts) {
            when (part.mimeType.fullType) {
                TEXT_HTML -> {
                    body = part.text
                }
                TEXT_PLAIN -> {
                    body = "<pre>${part.text}</pre>"
                }
                MULTIPART_ALTERNATIVE -> {
                    body = processAlternativePart(part.mimeType, part.text)
                }
                else -> {
                    when (part.encoding) {
                        ContentTransferEncoding.BASE64 ->
                            attachments.add(
                                ParsedAttachment(
                                    part.mimeType.parameters["name"] ?: "",
                                    part.mimeType.fullType,
                                    part.text.decodeBase64() ?: byteArrayOf(),
                                )
                            )
                        else -> error("Unknown encoding: ${part.encoding}")
                    }
                }
            }
        }
        return ParsedBody(body ?: "", attachments)
    }

    private fun parseMultipartAlternativePart(
        text: String,
        boundary: String
    ): List<ParsedBodyPart> {
        val parts = text.split("--$boundary")
        check(parts[0] == "") { "Text does not start with boundary" }
        check(parts.last() == "--" || parts.last() == "--\r\n" || parts.last() == "--\r\n\r\n") {
            "Text does not end with boundary, last part is: ${parts.last()}"
        }
        // Remove first and last parts which have no content (see check above)
        return parts.subList(1, parts.lastIndex).map { partText ->
            val (headers, body) = partText.trim().split("\r\n\r\n", limit = 2)
            val headersMap = parseHeaders(headers.trim())
            ParsedBodyPart(body, headersMap)
        }
    }

    private fun processAlternativePart(
        mimeType: MimeType,
        text: String,
    ): String {
        val boundary =
            mimeType.parameters["boundary"] ?: error("No boundary for ${mimeType.fullType}")
        val parts = parseMultipartAlternativePart(text, boundary)
        val textPart = parts.find { part -> part.mimeType.fullType == TEXT_HTML } ?: parts.first()
        return textPart.text
    }

    private data class ParsedBodyPart(val text: String, val headers: Headers) {
        val encoding: ContentTransferEncoding? =
            headers["Content-Transfer-Encoding"]?.let { ContentTransferEncoding.fromRaw(it) }
    }

    private val ParsedBodyPart.mimeType: MimeType
        get() = headers["Content-Type"]?.let { parseMimeType(it) }
            ?: MimeType("text", "plain", mapOf())
}