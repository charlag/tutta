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

//sequence-set    = (seq-number / seq-range) *("," sequence-set)
//                    ; set of seq-number values, regardless of order.
//                    ; Servers MAY coalesce overlaps and/or execute the
//                    ; sequence in any order.
//                    ; Example: a message sequence number set of
//                    ; 2,4:7,9,12:* for a mailbox with 15 messages is
//                    ; equivalent to 2,4,5,6,7,9,12,13,14,15
//                    ; Example: a message sequence number set of *:4,5:7
//                    ; for a mailbox with 10 messages is equivalent to
//                    ; 10,9,8,7,6,5,4,5,6,7 and MAY be reordered and
//                    ; overlap coalesced to be 4,5,6,7,8,9,10.
sealed class IdParam {
    data class Id(val id: Int) : IdParam()
    data class ClosedRange(val startId: Int, val endId: Int) : IdParam()
    data class EndOpenRange(val startId: Int) : IdParam()
    data class StartOpenRange(val endId: Int) : IdParam()
}

data class FetchCommand(
    val ids: List<IdParam>,
    val attrs: List<FetchAttr>,
)

val idParser: Parser<IdParam>
    get() {
        val endOpenRangeParser =
            (numberParser + characterParser(':').throwAway() + characterParser('*').throwAway())
                .map { start -> IdParam.EndOpenRange(start) }.named("endOpenRange")
        val startOpenRangeParser =
            (characterParser('*').throwAway() + characterParser(':').throwAway() + numberParser)
                .map { start -> IdParam.StartOpenRange(start) }.named("startOpenRange")
        val closedRangeParser = (numberParser + characterParser(':').throwAway() + numberParser)
            .map { (start, end) -> IdParam.ClosedRange(start, end) }.named("closedRange")
        val idSingleParser = numberParser
            .map(IdParam::Id).named("single")
        return (startOpenRangeParser or endOpenRangeParser or closedRangeParser or idSingleParser).named("idParser")
    }

val idSeqParser: Parser<List<IdParam>>
    get() = idParser.separatedBy(characterParser(','))

fun fetchAttrListParser(): Parser<List<FetchAttr>> =
    (characterParser('(').throwAway() + fetchAttrsParser() + characterParser(')').throwAway())
        .named("fetchAttrListParser")

// 1:1 (UID FLAGS INTERNALDATE RFC822.SIZE BODY.PEEK[HEADER.FIELDS (DATE FROM SENDER SUBJECT TO CC MESSAGE-ID REFERENCES CONTENT-TYPE CONTENT-DESCRIPTION IN-REPLY-TO REPLY-TO LINES LIST-POST X-LABEL)])
// 1 BODY.PEEK[]
val fetchCommandParser: Parser<FetchCommand>
    get() = (idSeqParser + characterParser(' ').throwAway() + anyFetchAttrsParser()).map { (ids, attrs) ->
        FetchCommand(ids, attrs)
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
    get() = oneOrMoreParser(
        characterParser('\\') or characterInRangeParser('a'..'z') or characterInRangeParser('A'..'Z')
    ).map { it.joinToString(separator = "") }

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
    val id: List<IdParam>,
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
    get() = (idSeqParser +
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