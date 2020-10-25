package com.charlag.tuta

import com.charlag.mailutil.toRFC3501
import com.charlag.mailutil.toRFC822
import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.tutanota.EncryptedMailAddress
import com.charlag.tuta.entities.tutanota.MailAddress
import kotlinx.cinterop.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import platform.posix.memset

inline fun <reified T : CVariable> T.zeroOut() = memset(this.ptr, 0, sizeOf<T>().convert())

fun EncryptedMailAddress.toMailAddress() =
    MailAddress(address = address, name = name, contact = null)

fun ByteArray.copyOfNumber(from: Int, number: Int): ByteArray =
    copyOfRange(kotlin.math.min(from, size), kotlin.math.min(from + number, size))

fun <T> List<T>.headTail(): Pair<T?, List<T>> = firstOrNull() to drop(1)

fun String.surroundWith(s: CharSequence): String = "$s$this$s"

fun String.quote() = replace("\"", "\\").surroundWith("\"")

fun Date.toInstant() = Instant.fromEpochMilliseconds(this.millis)

fun Date.toRFC822(): String = this.toInstant()
    .toLocalDateTime(TimeZone.currentSystemDefault())
    .toRFC822()

fun Date.toRFC3501(): String =
    this.toInstant().toLocalDateTime(TimeZone.currentSystemDefault())
        .toRFC3501()


fun ElementEntity.getId(): Id = this._id ?: error("No id! $this")
fun ListElementEntity.getId(): IdTuple = this._id ?: error("No id! $this")


/**
 * This is a version of the function which is missing in standard library. It initializes members
 * of the array.
 * If possible, allocate array of correct elements instead. It might not be possible with C
 * structures.
 */
inline fun <reified T : CVariable> CArrayPointer<T>.setAt(index: Int, value: CValue<T>) {
    value.write(this.rawValue + index * sizeOf<T>())
}