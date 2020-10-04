package com.charlag.tuta.imap

fun fetchAttrNameParser(): Parser<String> =
    makeOneOrMoreParser(
        characterInRangeParser('A'..'Z')
                or characterParser('.')
                or oneOfCharactersParser(digits())
                or characterParser('-')
    ).map { it.joinToString("") }

sealed class FetchAttr {
    data class Simple(val value: String) : FetchAttr()
    data class Parametrized(
        val value: String,
        val sectionSpec: SectionSpec,
        val range: LongRange?
    ) : FetchAttr()
}

data class SectionSpec(val section: String, val fields: List<String>)

// [HEADER.FIELDS (DATE FROM SENDER SUBJECT TO -REPLY-TO REPLY-TO LINES LIST-POST)]
fun sectionParser(): Parser<SectionSpec> =
    (characterParser('[').throwAway()
            + fetchAttrNameParser()
            + characterParser(' ').throwAway()
            + characterParser('(').throwAway()
            + separatedParser(characterParser(' '), fetchAttrNameParser())
            + characterParser(')').throwAway()
            + characterParser(']').throwAway()
            ).map { (section, fields) ->
            SectionSpec(section, fields)
        }

private fun fetchAttrParser(): Parser<FetchAttr> =
    (fetchAttrNameParser() + sectionParser().optional()).map { (name, section) ->
        if (section == null) {
            FetchAttr.Simple(name)
        } else {
            FetchAttr.Parametrized(name, section, null)
        }
    }

// UID FLAGS RFC822.SIZE BODY.PEEK[HEADER.FIELDS (DATE FROM SENDER SUBJECT TO CC REPLY-TO)]
fun fetchAttrsParser(): Parser<List<FetchAttr>> = separatedParser(
    separatorParser = characterParser(' '),
    valueParser = fetchAttrParser()
)

sealed class IdParam {
    data class Singe(val id: Int) : IdParam()
    data class Range(val startId: Int, val endId: Int) : IdParam()
}

data class FetchRequest(
    val idParam: IdParam,
    val field: List<FetchAttr>,
)

fun idParser(): Parser<IdParam> {
    val idRangeParser = (numberParser + characterParser(':').throwAway() + numberParser)
        .map { (start, end) -> IdParam.Range(start, end) }
    val idSingleParser = numberParser.map(IdParam::Singe)
    return idRangeParser or idSingleParser
}

// 1:1 (UID FLAGS INTERNALDATE RFC822.SIZE BODY.PEEK[HEADER.FIELDS (DATE FROM SENDER SUBJECT TO CC MESSAGE-ID REFERENCES CONTENT-TYPE CONTENT-DESCRIPTION IN-REPLY-TO REPLY-TO LINES LIST-POST X-LABEL)])
fun fetchCommandParser(): Parser<FetchRequest> =
    (idParser()
            + characterParser(' ').throwAway()
            + characterParser('(').throwAway()
            + fetchAttrsParser()
            + characterParser(')').throwAway()
            ).map { (id, attrs) -> FetchRequest(id, attrs) }