package com.charlag.tuta.imap

fun fetchAttrNameParser(): Parser<String> =
    makeOneOrMoreParser(
        characterInRangeParser('A'..'Z')
                or characterParser('.')
                or oneOfCharactersParser(digits())
                or characterParser('-')
                or characterInRangeParser('a'..'z')
    ).map { it.joinToString("") }.named("fetchAttrName")

sealed class FetchAttr {
    data class Simple(val value: String) : FetchAttr()
    data class Parametrized(
        val value: String,
        val sectionSpec: SectionSpec?,
        val range: LongRange?
    ) : FetchAttr()
}

data class SectionSpec(val section: String?, val fields: List<String>)

private fun sectionContentParser(): Parser<SectionSpec> = (
        fetchAttrNameParser()
                + characterParser(' ').throwAway()
                + characterParser('(').throwAway()
                + separatedParser(characterParser(' '), fetchAttrNameParser())
                + characterParser(')').throwAway()
        ).map { (section, fields) -> SectionSpec(section, fields) }.named("sectionContentParser")

fun emptySectionParser(): Parser<SectionSpec> =
    (characterParser('[') + characterParser(']'))
        .map { SectionSpec(null, listOf()) }
        .named("emptySectionParser")

fun nonemptySectionParser(): Parser<SectionSpec?> =
    (characterParser('[').throwAway()
            + sectionContentParser().optional()
            + characterParser(']').throwAway()
            ).named("nonemptySectionParser")

// [HEADER.FIELDS (DATE FROM SENDER SUBJECT TO REPLY-TO REPLY-TO LINES LIST-POST)]
fun sectionParser(): Parser<SectionSpec?> =
    (emptySectionParser() or nonemptySectionParser())
        .named("sectionParser")

private fun fetchAttrParser(): Parser<FetchAttr> =
    (fetchAttrNameParser() + sectionParser().optional()).map { (name, section) ->
        if (section == null) {
            FetchAttr.Simple(name)
        } else {
            FetchAttr.Parametrized(name, section, null)
        }
    }.named("fetchAttrParser")

// UID FLAGS RFC822.SIZE BODY.PEEK[HEADER.FIELDS (DATE FROM SENDER SUBJECT TO CC REPLY-TO)]
fun fetchAttrsParser(): Parser<List<FetchAttr>> = separatedParser(
    separatorParser = characterParser(' '),
    valueParser = fetchAttrParser()
)

// TODO: support sequence of IDs like "1588598392,1588598417,1589270478"
sealed class IdParam {
    data class Singe(val id: Int) : IdParam()
    data class ClosedRange(val startId: Int, val endId: Int) : IdParam()
    data class OpenRange(val startId: Int) : IdParam()
}

data class FetchRequest(
    val idParam: IdParam,
    val attrs: List<FetchAttr>,
)

fun idParser(): Parser<IdParam> {
    val openRangeParser =
        (numberParser + characterParser(':').throwAway() + characterParser('*').throwAway())
            .map { start -> IdParam.OpenRange(start) }.named("OpenRange")
    val idRangeParser = (numberParser + characterParser(':').throwAway() + numberParser)
        .map { (start, end) -> IdParam.ClosedRange(start, end) }.named("closedRange")
    val idSingleParser = numberParser.map(IdParam::Singe).named("single")
    return (openRangeParser or idRangeParser or idSingleParser).named("idParser")
}

fun fetchAttrListParser(): Parser<List<FetchAttr>> =
    (characterParser('(').throwAway() + fetchAttrsParser() + characterParser(')').throwAway())
        .named("fetchAttrListParser")

// 1:1 (UID FLAGS INTERNALDATE RFC822.SIZE BODY.PEEK[HEADER.FIELDS (DATE FROM SENDER SUBJECT TO CC MESSAGE-ID REFERENCES CONTENT-TYPE CONTENT-DESCRIPTION IN-REPLY-TO REPLY-TO LINES LIST-POST X-LABEL)])
// 1 BODY.PEEK[]
fun fetchCommandParser(): Parser<FetchRequest> =
    (idParser() + characterParser(' ').throwAway() + anyFetchAttrsParser()).map { (id, attrs) ->
        FetchRequest(id, attrs)
    }.named("fetchCommand")

private fun anyFetchAttrsParser() = (fetchAttrListParser() or fetchAttrParser().map { listOf(it) })