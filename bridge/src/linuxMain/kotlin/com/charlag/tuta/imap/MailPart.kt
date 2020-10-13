package com.charlag.tuta.imap

import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.tutanota.File
import com.charlag.tuta.entities.tutanota.Mail

sealed class Part {
    abstract val mime: String

    /**
     * pos is zero-indexed, like normal collections, not like MIME children from 1
     */
    abstract fun childAt(pos: Int): Part?

    data class Multipart(val parts: List<Part>) : Part() {
        override val mime: String
            get() = "multipart/mixed"

        override fun childAt(pos: Int): Part? = parts.getOrNull(pos)
    }

    data class TextHtml(val bodyId: Id) : Part() {
        override val mime: String
            get() = "text/html"

        override fun childAt(pos: Int): Part? = null
    }

    data class AttachmentPart(val file: File) : Part() {
        override val mime: String
            get() = file.mimeType ?: "application/octet-stream"

        override fun childAt(pos: Int): Part? = null
    }
}

/**
 * Our emails are always generated with one root part. RFC3501 says all messages have at least one
 * part but email itself is not really a part, it doesn't have own mime type.
 * To simplify things we return a list of parts which could theoretically appear at the root of
 * email. Then when traversing the path like "1.2.MIME" we can start with the first number.
 */
fun mailToParts(mail: Mail, attachments: List<File>): List<Part> {
    // always either Multipart or TextHtml
    val part = if (mail.attachments.isEmpty()) {
        (Part.TextHtml(mail.body))
    } else {
        Part.Multipart(
            listOf(Part.TextHtml(mail.body)) + attachments.map { Part.AttachmentPart(it) }
        )
    }
    return listOf(part)
}