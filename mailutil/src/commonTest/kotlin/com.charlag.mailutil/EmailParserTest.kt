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
    fun `test multipart mixed name in Content-Type`() {
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

    @Test
    fun `test multipart mixed name in Content-Disposition`() {
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
Content-Type: image/png
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

    @Test
    fun `test empty body`() {
        val imageB64 =
            """iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAAAAAA6mKC9AAAACXBIWXMAAC4jAAAuIwF4pT92AAAA
B3RJTUUH5AoZCyY0yJ9r8AAAABx0RVh0Q29tbWVudABDcmVhdGVkIHdpdGggR2xpbXBzZe5OGAcA
AABQSURBVBjTpY27DoAwDAMvESP//7E8egwNFYURSx4cxWcAy6dq9ihrMwECMJCgBUCisB1g0KU3
Y1cNmVWM7mI8hoEk5srCC5KDVJ+fleT/4QLLfiX8MTLv2QAAAABJRU5ErkJggg=="""

        val text = """Date: Sun, 25 Oct 2020 15:27:18 +0100
From: John Doe <jdoe@machine.example>
Subject: test file again
To: Mary Smith <mary@example.net>
Message-Id: <1234@local.machine.example>
X-Mailer: geary/3.38.1
MIME-Version: 1.0
Content-Type: multipart/mixed; boundary="=-WcObNiPgzOfhGukU7azw"

--=-WcObNiPgzOfhGukU7azw
Content-Type: multipart/alternative; boundary="=-nBA+4mh5IsRda5bwtmAr"

--=-nBA+4mh5IsRda5bwtmAr
Content-Type: text/plain; charset=us-ascii; format=flowed




--=-nBA+4mh5IsRda5bwtmAr
Content-Type: text/html; charset=us-ascii

<div id="geary-body" dir="auto"><div><br></div></div>
--=-nBA+4mh5IsRda5bwtmAr--

--=-WcObNiPgzOfhGukU7azw
Content-Type: image/png
Content-Disposition: attachment; filename=some-file.png
Content-Transfer-Encoding: base64

$imageB64

--=-WcObNiPgzOfhGukU7azw--
""".replace("\n", "\r\n")

        val parsed =parseEmail(text)

        assertEquals(
            ParsedEmail(
                mapOf(
                    "Date" to "Sun, 25 Oct 2020 15:27:18 +0100",
                    "From" to "John Doe <jdoe@machine.example>",
                    "Subject" to "test file again",
                    "To" to "Mary Smith <mary@example.net>",
                    "Message-Id" to "<1234@local.machine.example>",
                    "X-Mailer" to "geary/3.38.1",
                    "MIME-Version" to "1.0",
                    "Content-Type" to "multipart/mixed; boundary=\"=-WcObNiPgzOfhGukU7azw\"",
                ),
                ParsedBody(
                    "<div id=\"geary-body\" dir=\"auto\"><div><br></div></div>",
                    listOf(ParsedAttachment("some-file.png", "image/png", imageB64.decodeBase64()!!))
                )
            ),
            parsed
        )
    }
}