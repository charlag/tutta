package com.charlag.mailutil

import com.charlag.mailutil.*
import com.charlag.mailutil.DateSpec
import com.charlag.mailutil.FetchAttr
import com.charlag.mailutil.FetchCommand
import com.charlag.mailutil.FlagOperation
import com.charlag.mailutil.IdParam
import com.charlag.mailutil.ParserError
import com.charlag.mailutil.SearchCriteria
import com.charlag.mailutil.SectionSpec
import com.charlag.mailutil.StoreCommand
import kotlinx.datetime.Month
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ParserTest {
    @Test
    fun testParseNumber() {
        assertEquals(123456, com.charlag.mailutil.numberParser.build()("123456"))
    }

    @Test
    fun testParseCharacterInRange() {
        val parser = com.charlag.mailutil.characterInRangeParser('A'..'C').build()
        assertEquals('A', parser("A"))
        assertEquals('B', parser("B"))
        assertEquals('C', parser("C"))
        assertFailsWith<ParserError> { parser("@") }
        assertFailsWith<ParserError> { parser("?") }
    }

    @Test
    fun testUppercaseAsciiWordParser() {
        val parser =
            com.charlag.mailutil.oneOrMoreParser(com.charlag.mailutil.characterInRangeParser('A'..'Z'))
                .map { it.joinToString("") }.build()
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
        val parser = (com.charlag.mailutil.characterParser('A') + com.charlag.mailutil.characterParser(
            'B'
        )).build()
        assertEquals('A' to 'B', parser("AB"))
    }

    @Test
    fun testFetchCommandParserSimple() {
        val parser = com.charlag.mailutil.fetchAttrsParser().build()
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
        val parser = com.charlag.mailutil.fetchAttrsParser().build()
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
        val parser = com.charlag.mailutil.fetchCommandParser.build()
        assertEquals(
            FetchCommand(
                listOf(IdParam.ClosedRange(1, 1)),
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
    fun testFetchCommandBodyPartPartialParser() {
        val parser = com.charlag.mailutil.fetchCommandParser.build()
        assertEquals(
            FetchCommand(
                listOf(1602340968, 1602341271, 1602341428).map(IdParam::Id),
                listOf(FetchAttr.Parametrized("body.peek", SectionSpec("1", listOf()), 0 to 256))
            ),
            parser("1602340968,1602341271,1602341428 body.peek[1]<0.256>")
        )
    }

    @Test
    fun testEmptyFetchSpec() {
        val parser = com.charlag.mailutil.fetchCommandParser.build()
        assertEquals(
            FetchCommand(
                listOf(IdParam.Id(0)),
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
        val parser = com.charlag.mailutil.fetchCommandParser.build()
        assertEquals(
            FetchCommand(
                listOf(IdParam.Id(0)),
                listOf(FetchAttr.Simple("RFC8222.SIZE")),
            ),
            parser("0 RFC8222.SIZE")
        )
    }

    @Test
    fun testOpenRangeIdFetchSpec() {
        val parser = com.charlag.mailutil.fetchCommandParser.build()
        assertEquals(
            FetchCommand(
                listOf(IdParam.EndOpenRange(1)),
                listOf(FetchAttr.Simple("RFC8222.SIZE")),
            ),
            parser("1:* RFC8222.SIZE")
        )
    }

    @Test
    fun testClosedRangeIdFetchSpec() {
        val parser = com.charlag.mailutil.fetchCommandParser.build()
        assertEquals(
            FetchCommand(
                listOf(IdParam.ClosedRange(1, 5)),
                listOf(FetchAttr.Simple("RFC8222.SIZE")),
            ),
            parser("1:5 RFC8222.SIZE")
        )
    }

    @Test
    fun testSetIdFetchSpec() {
        val parser = com.charlag.mailutil.fetchCommandParser.build()
        assertEquals(
            FetchCommand(
                listOf(0, 2, 3).map(IdParam::Id),
                listOf(FetchAttr.Simple("RFC8222.SIZE")),
            ),
            parser("0,2,3 RFC8222.SIZE")
        )
    }

    @Test
    fun testSetAndRangeFetchSpec() {
        val parser = com.charlag.mailutil.fetchCommandParser.build()
        assertEquals(
            FetchCommand(
                listOf(0, 2).map(IdParam::Id) + IdParam.ClosedRange(3, 5),
                listOf(FetchAttr.Simple("RFC8222.SIZE")),
            ),
            parser("0,2,3:5 RFC8222.SIZE")
        )
    }


    @Test
    fun testSetAndOpenRangeFetchSpec() {
        val parser = com.charlag.mailutil.fetchCommandParser.build()
        assertEquals(
            FetchCommand(
                listOf(0, 2).map(IdParam::Id) + IdParam.EndOpenRange(3),
                listOf(FetchAttr.Simple("RFC8222.SIZE")),
            ),
            parser("0,2,3:* RFC8222.SIZE")
        )
    }


    @Test
    fun testRangeAndSetFetchSpec() {
        val parser = com.charlag.mailutil.fetchCommandParser.build()
        assertEquals(
            FetchCommand(
                listOf(IdParam.StartOpenRange(2)) + IdParam.Id(3) + IdParam.Id(4),
                listOf(FetchAttr.Simple("RFC8222.SIZE")),
            ),
            parser("*:2,3,4 RFC8222.SIZE")
        )
    }


    @Test
    fun testSetAndMiddleRangeFetchSpec() {
        val parser = com.charlag.mailutil.fetchCommandParser.build()
        assertEquals(
            FetchCommand(
                listOf(IdParam.Id(0)) + IdParam.ClosedRange(1, 3) + IdParam.Id(5),
                listOf(FetchAttr.Simple("RFC8222.SIZE")),
            ),
            parser("0,1:3,5 RFC8222.SIZE")
        )
    }

    @Test
    fun `parse search since`() {
        val parser = com.charlag.mailutil.searchCommandParser.build()
        assertEquals(
            listOf(SearchCriteria.Since(DateSpec(27, Month.SEPTEMBER, 2020))),
            parser("since 27-Sep-2020")
        )
    }

    @Test
    fun `parse search id range`() {
        val parser = com.charlag.mailutil.searchCommandParser.build()
        assertEquals(
            listOf(SearchCriteria.Id(IdParam.ClosedRange(1, 100))),
            parser("1:100")
        )
    }

    @Test
    fun `parse search uid range`() {
        val parser = com.charlag.mailutil.searchCommandParser.build()
        assertEquals(
            listOf(SearchCriteria.Uid(IdParam.ClosedRange(2, 50))),
            parser("uid 2:50")
        )
    }

    @Test
    fun `parse search since and uid range`() {
        val parser = com.charlag.mailutil.searchCommandParser.build()
        assertEquals(
            listOf(
                SearchCriteria.Since(DateSpec(3, Month.OCTOBER, 2020)),
                SearchCriteria.Uid(IdParam.ClosedRange(1, 1602407810)),
            ),
            parser("since 03-Oct-2020 uid 1:1602407810")
        )
    }

    @Test
    fun `test parse store command with adding a flag`() {
        val parser = com.charlag.mailutil.storeCommandParser.build()
        assertEquals(
            StoreCommand(
                listOf(IdParam.Id(1601570340)),
                FlagOperation.ADD,
                silent = false,
                flags = listOf("\\seen")
            ),
            parser("1601570340 +flags (\\seen)")
        )
    }

    @Test
    fun `test parse store command with replacing a flag`() {
        val parser = com.charlag.mailutil.storeCommandParser.build()
        assertEquals(
            StoreCommand(
                listOf(IdParam.Id(1601570340)),
                FlagOperation.REPLACE,
                silent = false,
                flags = listOf("\\seen")
            ),
            parser("1601570340 flags (\\seen)")
        )
    }

    @Test
    fun `test parse store command with removing a flag`() {
        val parser = com.charlag.mailutil.storeCommandParser.build()
        assertEquals(
            StoreCommand(
                listOf(IdParam.Id(1601570340)),
                FlagOperation.REMOVE,
                silent = false,
                flags = listOf("\\seen")
            ),
            parser("1601570340 -flags (\\seen)")
        )
    }

    @Test
    fun `test parse store command with adding a flag silent`() {
        val parser = com.charlag.mailutil.storeCommandParser.build()
        assertEquals(
            StoreCommand(
                listOf(IdParam.Id(1601570340)),
                FlagOperation.ADD,
                silent = true,
                flags = listOf("\\seen")
            ),
            parser("1601570340 +flags.silent (\\seen)")
        )
    }
}