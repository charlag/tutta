/**
 * Small parser library.
 */
package com.charlag.tuta.imap

import kotlin.math.max
import kotlin.math.min

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
 * Converts function which accepts contexts into a function which accepts a string
 */
fun <T> Parser<T>.asFunction(): ((String) -> T) = { s -> this(ParserContext(s)) }

/**
 * Transform parser into another parser. Do [mapper] on the result of parsing.
 */
fun <T, R> Parser<T>.map(mapper: (T) -> R): Parser<R> {
    return { iterator -> mapper(this(iterator)) }
}

fun <T, R> mapParser(parser: Parser<T>, mapper: (T) -> R): Parser<R> {
    return { iterator -> mapper(parser(iterator)) }
}

/**
 * Reads one character.
 */
fun characterParser(character: Char): Parser<Char> {
    return { iterator ->
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
            throw ParserError(
                "expected character $character got $value near ${
                    iterator.iteratee.substring(
                        sliceStart,
                        sliceEnd
                    )
                }"
            )
        }
    }
}

/**
 * Parses a (possibly empty) sequence.
 */
fun <T> zeroOrMoreParser(anotherParser: Parser<T>): Parser<List<T>> {
    return { iterator ->
        val result = mutableListOf<T>()
        try {
            var parseResult = anotherParser(iterator)
            while (true) {
                result.add(parseResult)
                parseResult = anotherParser(iterator)
            }
        } catch (e: ParserError) {
        }
        result
    }
}

/**
 * Parses a non-empty sequence.
 */
fun <T> makeOneOrMoreParser(parser: Parser<T>): Parser<List<T>> {
    return mapParser(zeroOrMoreParser(parser), { value ->
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
        try {
            this(iterator)
        } catch (e: ParserError) {
            null
        }
    }
}


/**
 * Parses a non-empty sequence separated by [separatorParser] in between.
 */
fun <S, T> separatedParser(separatorParser: Parser<S>, valueParser: Parser<T>): Parser<List<T>> {
    return { iterator ->
        val result = mutableListOf<T>()
        result.add(valueParser(iterator))
        while (true) {
            try {
                separatorParser(iterator)
            } catch (e: ParserError) {
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
        val iteratorPosition = iterator.position
        try {
            parserA(iterator)
        } catch (e: ParserError) {
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
        if (!iterator.hasNext()) {
            throw ParserError("No more input, could not parse one of $allowed")
        }
        val value = iterator.peek()
        if (allowed.contains(value)) {
            iterator.next()
            value
        } else {
            throw ParserError("Expected one of $allowed, got $value")
        }
    }
}

fun digits() = setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
val numberParser: Parser<Int> =
    mapParser(makeOneOrMoreParser(oneOfCharactersParser(digits()))) { values ->
        values.joinToString(separator = "").toInt()
    }

fun characterInRangeParser(range: CharRange): Parser<Char> = { iterator ->
    if (!iterator.hasNext()) {
        throw ParserError("No more input, could not parse in $range")
    }
    val value = iterator.peek()
    if (range.contains(value)) {
        iterator.next()
        value
    } else {
        throw ParserError("Expected one in $range, got $value")
    }
}