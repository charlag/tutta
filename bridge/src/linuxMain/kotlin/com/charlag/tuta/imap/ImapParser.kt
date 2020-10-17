package com.charlag.tuta.imap

import com.charlag.tuta.monthFromName
import kotlinx.datetime.Month

fun fetchAttrNameParser(): Parser<String> =
    oneOrMoreParser(
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
        val range: Pair<Int, Int>?
    ) : FetchAttr()
}

data class SectionSpec(val section: String?, val fields: List<String>)

// HEADER.FIELDS (DATE FROM)
private fun sectionContentParser(): Parser<SectionSpec> = (
        fetchAttrNameParser()
                + (characterParser(' ').throwAway()
                + characterParser('(').throwAway()
                + separatedParser(characterParser(' '), fetchAttrNameParser())
                + characterParser(')').throwAway()).optional().map { it ?: listOf() }
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


// "BODY.PEEK" section ["<" number "." nz-number ">"]
val fetchPartialParser: Parser<Pair<Int, Int>>
    get() = characterParser('<').throwAway() +
            numberParser +
            characterParser('.').throwAway() +
            numberParser +
            characterParser('>').throwAway()

private fun fetchAttrParser(): Parser<FetchAttr> =
    (fetchAttrNameParser() +
            sectionParser().optional() +
            fetchPartialParser.optional()
            ).map { (nameAndSection, partial) ->
            val (name, section) = nameAndSection
            if (section == null) {
                FetchAttr.Simple(name)
            } else {
                FetchAttr.Parametrized(name, section, partial)
            }
        }.named("fetchAttrParser")

// UID FLAGS RFC822.SIZE BODY.PEEK[HEADER.FIELDS (DATE FROM SENDER SUBJECT TO CC REPLY-TO)]
fun fetchAttrsParser(): Parser<List<FetchAttr>> = separatedParser(
    separatorParser = characterParser(' '),
    valueParser = fetchAttrParser()
)

sealed class IdParam {
    data class IdSet(val ids: List<Int>) : IdParam()
    data class ClosedRange(val startId: Int, val endId: Int) : IdParam()
    data class OpenRange(val startId: Int) : IdParam()
}

data class FetchRequest(
    val idParam: IdParam,
    val attrs: List<FetchAttr>,
)

val idParser: Parser<IdParam>
    get() {
        val openRangeParser =
            (numberParser + characterParser(':').throwAway() + characterParser('*').throwAway())
                .map { start -> IdParam.OpenRange(start) }.named("OpenRange")
        val idRangeParser = (numberParser + characterParser(':').throwAway() + numberParser)
            .map { (start, end) -> IdParam.ClosedRange(start, end) }.named("closedRange")
        val idSingleParser = separatedParser(characterParser(','), numberParser)
            .map(IdParam::IdSet).named("single")
        return (openRangeParser or idRangeParser or idSingleParser).named("idParser")
    }

fun fetchAttrListParser(): Parser<List<FetchAttr>> =
    (characterParser('(').throwAway() + fetchAttrsParser() + characterParser(')').throwAway())
        .named("fetchAttrListParser")

// 1:1 (UID FLAGS INTERNALDATE RFC822.SIZE BODY.PEEK[HEADER.FIELDS (DATE FROM SENDER SUBJECT TO CC MESSAGE-ID REFERENCES CONTENT-TYPE CONTENT-DESCRIPTION IN-REPLY-TO REPLY-TO LINES LIST-POST X-LABEL)])
// 1 BODY.PEEK[]
fun fetchCommandParser(): Parser<FetchRequest> =
    (idParser + characterParser(' ').throwAway() + anyFetchAttrsParser()).map { (id, attrs) ->
        FetchRequest(id, attrs)
    }.named("fetchCommand")

private fun anyFetchAttrsParser() = (fetchAttrListParser() or fetchAttrParser().map { listOf(it) })

fun characterNotParser(char: Char): Parser<Char> = { context ->
    if (!context.hasNext()) {
        throw ParserError("No more input for 'not char $char'")
    }
    context.next().also {
        if (it == char) throw ParserError("Char not '$char'")
    }
}

val quotedStringParser: Parser<String>
    get() = characterParser('"').throwAway() +
            zeroOrMoreParser(characterNotParser('"')).map { it.joinToString("") } +
            characterParser('"').throwAway()

val quotedOrUnquotedStringParser: Parser<String>
    get() = quotedStringParser or
            oneOrMoreParser(characterNotParser(' ')).map { it.joinToString("") }

data class ListCommand(val delimiter: String, val pattern: String)

val listCommandParser: Parser<ListCommand>
    get() = (quotedOrUnquotedStringParser + characterParser(' ').throwAway() + quotedOrUnquotedStringParser)
        .map { (delimiter, pattern) -> ListCommand(delimiter, pattern) }

val flagParser: Parser<String>
    get() = (characterParser('\\').throwAway() +
            oneOrMoreParser(
                characterInRangeParser('a'..'z') or characterInRangeParser('A'..'Z')
            ).map { it.joinTo(StringBuilder("\\"), separator = "").toString() })

val flagsParser: Parser<List<String>>
    get() = characterParser('(').throwAway() +
            separatedParser(characterParser(' '), flagParser) +
            characterParser(')').throwAway()

data class AppendCommand(val targetFolder: String, val flags: List<String>, val literalSize: Int)

val appendParser: Parser<AppendCommand>
    get() = (oneOrMoreParser(characterNotParser(' ')).map { it.joinToString("") } +
            characterParser(' ').throwAway() +
            flagsParser +
            characterParser(' ').throwAway() +
            characterParser('{').throwAway() + numberParser + characterParser('}').throwAway())
        .map { (folderAndFlags, literalSize) ->
            AppendCommand(folderAndFlags.first, folderAndFlags.second, literalSize)
        }


data class StatusCommand(val folder: String, val attributes: List<String>)

val statusParser: Parser<StatusCommand>
    get() = (oneOrMoreParser(characterNotParser(' ')).map { it.joinToString("") } +
            characterParser(' ').throwAway() +
            characterParser('(').throwAway() +
            separatedParser(
                characterParser(' '),
                oneOrMoreParser(characterInRangeParser('A'..'Z')).map { it.joinToString("") }
            ) +
            characterParser(')').throwAway()
            ).map { (folder, flags) -> StatusCommand(folder, flags) }

data class DateSpec(val day: Int, val month: Month, val year: Int)

sealed class SearchCriteria {
    data class Since(val date: DateSpec) : SearchCriteria()
    data class Id(val idRange: IdParam) : SearchCriteria()
    data class Uid(val idSet: IdParam) : SearchCriteria()
}

/**
 * Case insensitive
 */
fun wordParser(word: String): Parser<String> = { context: ParserContext ->
    for (lowerChar in word.toLowerCase()) {
        if (!context.hasNext() || context.next().toLowerCase() != lowerChar) {
            throw ParserError("Could not parse word $word")
        }
    }
    word
}.named("wordParser($word)")

inline fun <T> ParserContext.takeExactly(number: Int, block: (Char) -> T) {
    var i = 0
    while (i < number) {
        if (!hasNext()) throw ParserError("Could not take $number from $this, no more input")
        block(next())
        i++
    }
}

val monthParser: Parser<Month>
    get() = ({ context: ParserContext ->
        val word = StringBuilder(3)
        context.takeExactly(3) { word.append(it) }
        monthFromName(word.toString()) ?: throw ParserError("$word is not a month name")
    }).named("month")

// 27-Sep-2020
val dateSpecParser: Parser<DateSpec>
    get() = (numberParser + characterParser('-').throwAway() +
            monthParser + characterParser('-').throwAway() +
            numberParser
            ).map { (dateAndMonth, year) ->
            DateSpec(dateAndMonth.first, dateAndMonth.second, year)
        }.named("dateSpec")

val searchCriteriaSinceParser: Parser<SearchCriteria.Since>
    get() = (wordParser("since").throwAway() +
            characterParser(' ').throwAway() +
            dateSpecParser
            ).map { SearchCriteria.Since(it) }

val searchCriteriaIdRangeParser: Parser<SearchCriteria.Id>
    get() = idParser.map { SearchCriteria.Id(it) }

val searchCriteriaUidRangeParser: Parser<SearchCriteria.Uid>
    get() = (wordParser("uid").throwAway() + characterParser(' ').throwAway() + idParser).map {
        SearchCriteria.Uid(it)
    }

val searchCriteriaParser: Parser<SearchCriteria>
    get() = (searchCriteriaSinceParser or
            searchCriteriaUidRangeParser or
            searchCriteriaIdRangeParser).named("searchCriteria")

// search since 27-Sep-2020
val searchCommandParser: Parser<List<SearchCriteria>>
    get() = separatedParser(characterParser(' '), searchCriteriaParser).named("searchCommand")


enum class FlagOperation {
    REPLACE, ADD, REMOVE
}

data class StoreCommand(
    val id: IdParam,
    val operation: FlagOperation,
    val silent: Boolean,
    val flags: List<String>
)

val flagOperationParser: Parser<FlagOperation>
    get() = (characterParser('+') or characterParser('-')).optional().map { v ->
        when (v) {
            null -> FlagOperation.REPLACE
            '+' -> FlagOperation.ADD
            '-' -> FlagOperation.REMOVE
            else -> error("Parsed wrong character for flag operation: $v")
        }
    }

fun <A, B, C> Parser<Pair<Pair<A, B>, C>>.flatten(): Parser<Triple<A, B, C>> {
    return map { (ab, c) -> Triple(ab.first, ab.second, c) }
}

fun <A, B, C, D> Parser<Pair<Pair<Pair<A, B>, C>, D>>.flatten(): Parser<FourTuple<A, B, C, D>> {
    return map { (abc, d) ->
        FourTuple(abc.first.first, abc.first.second, abc.second, d)
    }
}

data class FourTuple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

// store 1601570340 +flags (\seen)
val storeCommandParser: Parser<StoreCommand>
    get() = (idParser +
            characterParser(' ').throwAway() +
            flagOperationParser +
            wordParser("flags").throwAway() +
            wordParser(".silent").optional() +
            characterParser(' ').throwAway() +
            flagsParser)
        .flatten()
        .map { (id, operation, silent, flags) ->
            StoreCommand(id, operation, silent != null, flags)
        }