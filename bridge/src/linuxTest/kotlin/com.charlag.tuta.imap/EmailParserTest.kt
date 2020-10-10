package com.charlag.tuta.imap

import kotlin.test.Test
import kotlin.test.assertEquals

class EmailParserTest {
    @Test
    fun testSimple() {
        val text = """From: John Doe <jdoe@machine.example>
To: Mary Smith <mary@example.net>
Subject: Saying Hello
Date: Fri, 21 Nov 1997 09:55:06 -0600
Message-ID: <1234@local.machine.example>

This is a message just to say hello.
So, "Hello".""".replace("\n", "\r\n")

        println(text.split("\r\n\r\n", limit = 2))
        val parsed = parseEmail(text)
        assertEquals(
            ParsedEmail(
                mapOf(
                    "From" to "John Doe <jdoe@machine.example>",
                    "To" to "Mary Smith <mary@example.net>",
                    "Subject" to "Saying Hello",
                    "Date" to "Fri, 21 Nov 1997 09:55:06 -0600",
                    "Message-ID" to "<1234@local.machine.example>"
                ),
                body = "This is a message just to say hello.\r\nSo, \"Hello\"."
            ),
            parsed
        )
    }
}