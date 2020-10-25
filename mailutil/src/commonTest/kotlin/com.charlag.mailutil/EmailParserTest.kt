package com.charlag.mailutil

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
                ParsedBody(
                    "<pre>This is a message just to say hello.\r\nSo, \"Hello\".</pre>",
                    listOf()
                )
            ),
            parsed
        )
    }

    @Test
    fun testMultipartAlternative() {
        val text = """From: John Doe <jdoe@machine.example>
To: Mary Smith <mary@example.net>
Subject: Saying Hello
Date: Fri, 21 Nov 1997 09:55:06 -0600
Message-ID: <1234@local.machine.example>
Content-Type: multipart/alternative; boundary="=-FFRijKSf+yg20pHxywLy"

--=-FFRijKSf+yg20pHxywLy
Content-Type: text/plain; charset=us-ascii; format=flowed

sounds creepy


--=-FFRijKSf+yg20pHxywLy
Content-Type: text/html; charset=us-ascii

<div id="geary-body" dir="auto"><div>sounds creepy</div></div>
--=-FFRijKSf+yg20pHxywLy--
""".replace("\n", "\r\n")

        val parsed = parseEmail(text)
        assertEquals(
            ParsedEmail(
                mapOf(
                    "From" to "John Doe <jdoe@machine.example>",
                    "To" to "Mary Smith <mary@example.net>",
                    "Subject" to "Saying Hello",
                    "Date" to "Fri, 21 Nov 1997 09:55:06 -0600",
                    "Message-ID" to "<1234@local.machine.example>",
                    "Content-Type" to "multipart/alternative; boundary=\"=-FFRijKSf+yg20pHxywLy\"",
                ),
                ParsedBody(
                    "<div id=\"geary-body\" dir=\"auto\"><div>sounds creepy</div></div>",
                    listOf()
                )
            ),
            parsed
        )
    }

    @Test
    fun testMultipartMixed() {
        val imageB64 =
            """iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAAAAAA6mKC9AAAACXBIWXMAAC4jAAAuIwF4pT92AAAA
B3RJTUUH5AoZCyY0yJ9r8AAAABx0RVh0Q29tbWVudABDcmVhdGVkIHdpdGggR2xpbXBzZe5OGAcA
AABQSURBVBjTpY27DoAwDAMvESP//7E8egwNFYURSx4cxWcAy6dq9ihrMwECMJCgBUCisB1g0KU3
Y1cNmVWM7mI8hoEk5srCC5KDVJ+fleT/4QLLfiX8MTLv2QAAAABJRU5ErkJggg=="""

        val text = """From: John Doe <jdoe@machine.example>
To: Mary Smith <mary@example.net>
Subject: Saying Hello
Date: Fri, 21 Nov 1997 09:55:06 -0600
Message-ID: <1234@local.machine.example>
MIME-Version: 1.0
Content-Type: multipart/mixed; 
	boundary="----=_Part_29466_3240722.1602611127577"

------=_Part_29466_3240722.1602611127577
Content-Type: multipart/alternative; boundary="=-FFRijKSf+yg20pHxywLy"

--=-FFRijKSf+yg20pHxywLy
Content-Type: text/plain; charset=us-ascii; format=flowed

sounds creepy


--=-FFRijKSf+yg20pHxywLy
Content-Type: text/html; charset=us-ascii

<div id="geary-body" dir="auto"><div>sounds creepy</div></div>
--=-FFRijKSf+yg20pHxywLy--

------=_Part_29466_3240722.1602611127577
Content-Type: image/png; 
	name=image.png
Content-Transfer-Encoding: base64
Content-Disposition: attachment; 
	filename=image.png

$imageB64
------=_Part_29466_3240722.1602611127577--
""".replace("\n", "\r\n")

        val parsed = parseEmail(text)
        assertEquals(
            ParsedEmail(
                mapOf(
                    "From" to "John Doe <jdoe@machine.example>",
                    "To" to "Mary Smith <mary@example.net>",
                    "Subject" to "Saying Hello",
                    "Date" to "Fri, 21 Nov 1997 09:55:06 -0600",
                    "Message-ID" to "<1234@local.machine.example>",
                    "MIME-Version" to "1.0",
                    "Content-Type" to "multipart/mixed; boundary=\"----=_Part_29466_3240722.1602611127577\"",
                ),
                ParsedBody(
                    "<div id=\"geary-body\" dir=\"auto\"><div>sounds creepy</div></div>",
                    listOf(ParsedAttachment("image.png", "image/png", imageB64.decodeBase64()!!))
                )
            ),
            parsed
        )
    }
}