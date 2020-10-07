package com.charlag.tuta.imap

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ParserTests {
    @Test
    fun testParseNumber() {
        assertEquals(123456, numberParser.build()("123456"))
    }

    @Test
    fun testParseCharacterInRange() {
        val parser = characterInRangeParser('A'..'C').build()
        assertEquals('A', parser("A"))
        assertEquals('B', parser("B"))
        assertEquals('C', parser("C"))
        assertFailsWith<ParserError> { parser("@") }
        assertFailsWith<ParserError> { parser("?") }
    }

    @Test
    fun testUppercaseAsciiWordParser() {
        val parser =
            makeOneOrMoreParser(characterInRangeParser('A'..'Z')).map { it.joinToString("") }.build()
        assertEquals("ABC", parser("ABC"))
        assertEquals("A", parser("A"))
        assertEquals("XYZ", parser("XYZ"))
        assertEquals("INTERNALDATE", parser("INTERNALDATE"))
        assertFailsWith<ParserError> { parser("") }
        assertFailsWith<ParserError> { parser("abc") }
        assertFailsWith<ParserError> { parser("?") }
        assertFailsWith<ParserError> { parser("1") }
    }

    @Test
    fun testCompose() {
        val parser = (characterParser('A') + characterParser('B')).build()
        assertEquals('A' to 'B', parser("AB"))
    }

    @Test
    fun testFetchCommandParserSimple() {
        val parser = fetchAttrsParser().build()
        assertEquals(
            listOf(
                FetchAttr.Simple("UID"),
                FetchAttr.Simple("FLAGS"),
                FetchAttr.Simple("INTERNALDATE"),
                FetchAttr.Simple("RFC822.SIZE")
            ),
            parser("UID FLAGS INTERNALDATE RFC822.SIZE")
        )
    }

    @Test
    fun testParametrizedCommandParser() {
        val parser = fetchAttrsParser().build()
        assertEquals(
            listOf(
                FetchAttr.Simple("RFC822.SIZE"),
                FetchAttr.Parametrized(
                    "BODY.PEEK",
                    SectionSpec(
                        "HEADER.FIELDS",
                        listOf("DATE", "FROM", "SENDER", "SUBJECT", "TO", "CC", "REPLY-TO")
                    ),
                    range = null
                ),
            ),
            parser("RFC822.SIZE BODY.PEEK[HEADER.FIELDS (DATE FROM SENDER SUBJECT TO CC REPLY-TO)]")
        )
    }

    @Test
    fun testFetchCommandParser() {
        val parser = fetchCommandParser().build()
        assertEquals(
            FetchRequest(
                IdParam.ClosedRange(1, 1),
                listOf(
                    FetchAttr.Simple("UID"),
                    FetchAttr.Simple("RFC822.SIZE"),
                    FetchAttr.Parametrized(
                        "BODY.PEEK", SectionSpec(
                            "HEADER.FIELDS",
                            listOf("TO", "CONTENT-TYPE"),
                        ),
                        range = null
                    ),
                ),
            ),
            parser("1:1 (UID RFC822.SIZE BODY.PEEK[HEADER.FIELDS (TO CONTENT-TYPE)])")
        )
    }

    @Test
    fun testEmptyFetchSpec() {
        val parser = fetchCommandParser().build()
        assertEquals(
            FetchRequest(
                IdParam.IdSet(listOf(0)),
                listOf(
                    FetchAttr.Parametrized(
                        "BODY.PEEK",
                        sectionSpec = SectionSpec(null, listOf()),
                        range = null
                    ),
                ),
            ),
            parser("0 BODY.PEEK[]")
        )
    }

    @Test
    fun testSingleIdFetchSpec() {
        val parser = fetchCommandParser().build()
        assertEquals(
            FetchRequest(
                IdParam.IdSet(listOf(0)),
                listOf(FetchAttr.Simple("RFC8222.SIZE")),
            ),
            parser("0 RFC8222.SIZE")
        )
    }

    @Test
    fun testOpenRangeIdFetchSpec() {
        val parser = fetchCommandParser().build()
        assertEquals(
            FetchRequest(
                IdParam.OpenRange(1),
                listOf(FetchAttr.Simple("RFC8222.SIZE")),
            ),
            parser("1:* RFC8222.SIZE")
        )
    }

    @Test
    fun testClosedRangeIdFetchSpec() {
        val parser = fetchCommandParser().build()
        assertEquals(
            FetchRequest(
                IdParam.ClosedRange(1, 5),
                listOf(FetchAttr.Simple("RFC8222.SIZE")),
            ),
            parser("1:5 RFC8222.SIZE")
        )
    }

    @Test
    fun testSetIdFetchSpec() {
        val parser = fetchCommandParser().build()
        assertEquals(
            FetchRequest(
                IdParam.IdSet(listOf(0, 2, 3)),
                listOf(FetchAttr.Simple("RFC8222.SIZE")),
            ),
            parser("0,2,3 RFC8222.SIZE")
        )
    }
}