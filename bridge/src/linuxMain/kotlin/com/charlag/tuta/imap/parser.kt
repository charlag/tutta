/**
 * Small parser library.
 */
package com.charlag.tuta.imap

import kotlin.math.max
import kotlin.math.min

@Suppress("UNUSED_PARAMETER")
private fun log(parser: String, at: ParserContext?) {
    // Uncomment if you need verbose logging for parsers. Should probably use lambdas instead?

//    if (at != null) {
//        val char = if (at.position == at.iteratee.lastIndex) ""
//        else at.iteratee[at.position + 1].toString()
//
//        println("$parser at ${at.position + 1} '${char}'")
//    } else {
//        println(parser)
//    }
}

class ParserError(message: String) : Error(message)

/**
 * We are iterating over a string when parsing.
 */
class ParserContext(var iteratee: String) {
    var position = -1

    fun next(): Char {
        return this.iteratee[++this.position]
    }

    fun peek(): Char {
        return this.iteratee[this.position + 1]
    }

    fun hasNext(): Boolean {
        return this.iteratee.length - 1 > this.position
    }
}

/**
 * Parser is just a function which extracts things from context
 */
typealias Parser<T> = (ParserContext) -> T

/**
 * Converts function which accepts contexts into a function which accepts a string and check that
 * the whole input was consumed.
 */
fun <T> Parser<T>.build(): ((String) -> T) = { s ->
    val parserContext = ParserContext(s)
    this(parserContext).also {
        if (parserContext.hasNext()) {
            throw ParserError(
                "Did not consume the whole input, ${parserContext.position} out of ${parserContext.iteratee.length} near ${
                    s.substring(parserContext.position)
                }"
            )
        }
    }
}

/**
 * Transform parser into another parser. Do [mapper] on the result of parsing.
 */
fun <T, R> Parser<T>.map(mapper: (T) -> R): Parser<R> {
    return { iterator -> mapper(this(iterator)) }
}

fun <T> Parser<T>.named(name: String): Parser<T> = { iterator ->
    log("> $name", iterator)
    try {
        val result = this(iterator)
        log("< $name $result", iterator)
        result
    } catch (e: Throwable) {
        log("! $name $e", iterator)
        throw e
    }
}

fun <T, R> mapParser(parser: Parser<T>, mapper: (T) -> R): Parser<R> {
    return { iterator -> mapper(parser(iterator)) }
}

/**
 * Reads one character.
 */
fun characterParser(character: Char): Parser<Char> {
    return { iterator ->
        log("character $character", iterator)
        if (!iterator.hasNext()) {
            throw ParserError("No more input, wanted to parse $character")
        }
        val value = iterator.peek()
        if (value == character) {
            iterator.next()
            value
        } else {
            val sliceStart = max(iterator.position - 10, 0)
            val sliceEnd = min(iterator.position + 10, iterator.iteratee.length - 1)
            log("failed character $character", iterator)
            throw ParserError(
                "expected character '$character' got '$value' near '${
                    iterator.iteratee.substring(
                        sliceStart,
                        sliceEnd
                    )
                }', slice: ($sliceStart, $sliceEnd), char at: ${iterator.position + 1}"
            )
        }
    }
}

/**
 * Parses a (possibly empty) sequence.
 */
fun <T> zeroOrMoreParser(anotherParser: Parser<T>): Parser<List<T>> {
    return { iterator ->
        log("zeroOrMore $anotherParser", iterator)
        val result = mutableListOf<T>()
        var state = iterator.position
        try {
            var parseResult = anotherParser(iterator)
            state = iterator.position
            while (true) {
                result.add(parseResult)
                parseResult = anotherParser(iterator)
                state = iterator.position
            }
        } catch (e: ParserError) {
            iterator.position = state
            log("zeroOrMoreStopped $anotherParser", iterator)
        }
        result
    }
}

/**
 * Parses a non-empty sequence.
 */
fun <T> oneOrMoreParser(parser: Parser<T>): Parser<List<T>> {
    return mapParser(zeroOrMoreParser(parser), { value ->
        log("makeOneOrMoreParser $parser", null)
        if (value.isEmpty()) {
            throw ParserError("Expected at least one value, got none")
        }
        value
    })
}

/**
 * Takes a parser and converts it into an optional parser.
 */
fun <T> Parser<T>.optional(): Parser<T?> {
    return { iterator ->
        log("optional", iterator)
        val position = iterator.position
        try {
            this(iterator)
        } catch (e: ParserError) {
            log("optional failed, backtracking to ${iterator.position}", iterator)
            iterator.position = position
            null
        }
    }
}


/**
 * Parses a non-empty sequence separated by [separatorParser] in between.
 */
fun <S, T> separatedParser(separatorParser: Parser<S>, valueParser: Parser<T>): Parser<List<T>> {
    return { iterator ->
        log("separatedParser", iterator)
        val result = mutableListOf<T>()
        result.add(valueParser(iterator))
        while (true) {
            try {
                separatorParser(iterator)
            } catch (e: ParserError) {
                log("separatedParser stopped $e", iterator)
                break
            }
            result.add(valueParser(iterator))
        }
        result
    }
}

/**
 * Takes two parsers and makes a new one, which will try the first parser snd when it fails it will
 * try the second one.
 */
fun <A> eitherParser(parserA: Parser<A>, parserB: Parser<A>): Parser<A> {
    return { iterator ->
        log("eitherParser", iterator)
        val iteratorPosition = iterator.position
        try {
            parserA(iterator)
        } catch (e: ParserError) {
            log("eitherParser right $e", iterator)
            iterator.position = iteratorPosition
            parserB(iterator)
        }
    }
}

infix fun <A> Parser<A>.or(parserB: Parser<A>) = eitherParser(this, parserB)

/**
 * Takes a parser and converts it into an empty parser. Useful for throwing away syntax like
 * parenthesys.
 */
fun <T> Parser<T>.throwAway(): Parser<Unit> = map { }

/**
 * Parses [this] first and then [parserB] and returns its result.
 */
operator fun <B> Parser<Unit>.plus(parserB: Parser<B>): Parser<B> = { iterator ->
    this(iterator)
    parserB(iterator)
}


/**
 * Parses [this] first and then [parserB] and throws away its result.
 */
operator fun <B> Parser<B>.plus(parserB: Parser<Unit>): Parser<B> = { iterator ->
    log("plus", iterator)
    val result = this(iterator)
    parserB(iterator)
    result
}

/**
 * Parses [this] first and then [parserB] and returns both.
 */
operator fun <A, B> Parser<A>.plus(parserB: Parser<B>): Parser<Pair<A, B>> = { iterator ->
    this(iterator) to parserB(iterator)
}

/**
 * Parses one of the characters.
 */
fun oneOfCharactersParser(allowed: Collection<Char>): Parser<Char> {
    return { iterator ->
        log("oneOfCharactersParser $allowed", iterator)
        if (!iterator.hasNext()) {
            throw ParserError("No more input, could not parse one of $allowed")
        }
        val value = iterator.peek()
        if (allowed.contains(value)) {
            iterator.next()
            value
        } else {
            log("oneOfCharactersParser failed $allowed", iterator)
            throw ParserError("Expected one of $allowed, got $value")
        }
    }
}

fun digits() = setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
val numberParser: Parser<Int>
    get() = mapParser(oneOrMoreParser(oneOfCharactersParser(digits()))) { values ->
        log("numberParser", null)
        values.joinToString(separator = "").toInt()
    }

fun characterInRangeParser(range: CharRange): Parser<Char> = { iterator ->
    log("characterInRangeParser $range", iterator)
    if (!iterator.hasNext()) {
        log("characterInRangeParser no more input $range", iterator)
        throw ParserError("No more input, could not parse in $range")
    }
    val value = iterator.peek()
    if (range.contains(value)) {
        iterator.next()
        value
    } else {
        log("characterInRangeParser failed $range", iterator)
        throw ParserError("Expected one in '$range', got '$value', value at ${iterator.position + 1}")
    }
}