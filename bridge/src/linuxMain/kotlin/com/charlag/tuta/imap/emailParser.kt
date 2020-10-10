package com.charlag.tuta.imap

import com.charlag.tuta.entities.tutanota.MailAddress

data class ParsedEmail(val headers: Map<String, String>, val body: String)

fun parseEmail(text: String): ParsedEmail {
    val (headerPart, bodyPart) = text.split("\r\n\r\n", limit = 2)
        .also { if (it.size != 2) throw ParserError("Invalid mail, no empty line") }

    val headers = parseHeaders(headerPart)
    return ParsedEmail(headers, bodyPart)
}

fun parseHeaders(text: String): Map<String, String> {
    val buffer = StringBuilder(text)

    while (true) {
        val index = buffer.indexOf("\r\n ")
        if (index == -1) break
        buffer.deleteRange(index, index + 2)
    }
    val result = mutableMapOf<String, String>()
    for (line in buffer.split("\r\n")) {
        val (name, value) = line.split(": ", limit = 2)
            .also { if (it.size != 2) throw ParserError("Invalid header line: $line") }
        result[name] = value
    }
    return result
}

val mailAddressParser: Parser<MailAddress>
    get() = (zeroOrMoreParser(characterNotParser('<')).map { it.joinToString("") } +
            characterParser('<').throwAway() +
            oneOrMoreParser(characterNotParser('>')).map { it.joinToString("") } +
            characterParser('>').throwAway()).map { (name, address) ->
        MailAddress(
            name = name,
            address = address,
            contact = null
        )
    }