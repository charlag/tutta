import com.charlag.tuta.imap.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@Test
fun testParseNumber() {
    assertEquals(123456, numberParser("123456".iter()))
}

@Test
fun testParseCharacterInRange() {
    val parser = characterInRangeParser('A'..'C')
    assertEquals('A', parser("A".iter()))
    assertEquals('B', parser("B".iter()))
    assertEquals('C', parser("C".iter()))
    assertFailsWith<ParserError> { parser("@".iter()) }
    assertFailsWith<ParserError> { parser("?".iter()) }
}

@Test
fun testUppercaseAsciiWordParser() {
    val parser =
        makeOneOrMoreParser(characterInRangeParser('A'..'Z')).map { it.joinToString("") }
    assertEquals("ABC", parser("ABC".iter()))
    assertEquals("A", parser("A".iter()))
    assertEquals("XYZ", parser("XYZ".iter()))
    assertEquals("INTERNALDATE", parser("INTERNALDATE".iter()))
    assertFailsWith<ParserError> { parser("".iter()) }
    assertFailsWith<ParserError> { parser("abc".iter()) }
    assertFailsWith<ParserError> { parser("?".iter()) }
    assertFailsWith<ParserError> { parser("1".iter()) }
}

@Test
fun testCompose() {
    val parser = characterParser('A') + characterParser('B')
    assertEquals('A' to 'B', parser("AB".iter()))
}

@Test
fun testFetchCommandParserSimple() {
    val parser = fetchAttrsParser()
    assertEquals(
        listOf(
            FetchAttr.Simple("UID"),
            FetchAttr.Simple("FLAGS"),
            FetchAttr.Simple("INTERNALDATE"),
            FetchAttr.Simple("RFC822.SIZE")
        ),
        parser("UID FLAGS INTERNALDATE RFC822.SIZE".iter())
    )
}

@Test
fun testParametrizedCommandParser() {
    val parser = fetchAttrsParser()
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
        parser("RFC822.SIZE BODY.PEEK[HEADER.FIELDS (DATE FROM SENDER SUBJECT TO CC REPLY-TO)]".iter())
    )
}

@Test
fun testFetchCommandParser() {
    val parser = fetchCommandParser()
    assertEquals(
        FetchRequest(
            IdParam.Range(1, 1),
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
        parser("1:1 (UID RFC822.SIZE BODY.PEEK[HEADER.FIELDS (TO CONTENT-TYPE)])".iter())
    )
}


private fun String.iter() = ParserContext(this)