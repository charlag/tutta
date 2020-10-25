package com.charlag.mailutil

import kotlinx.datetime.*
import kotlin.native.concurrent.SharedImmutable

/**
 * Formats into date-time format defined by RFC822 (email headers). Looks like:
 *
 * 26 Aug 76 1429 +0200
 *
 * Always uses local time zone and numeric zone offset.
 */
fun LocalDateTime.toRFC822(): String {
    //      date-time   =  [ day "," ] date time        ; dd mm yy
    //                                                 ;  hh:mm:ss zzz
    //
    //     day         =  "Mon"  / "Tue" /  "Wed"  / "Thu"
    //                 /  "Fri"  / "Sat" /  "Sun"
    //
    //     date        =  1*2DIGIT month 2DIGIT        ; day month year
    //                                                 ;  e.g. 20 Jun 82
    //
    //     month       =  "Jan"  /  "Feb" /  "Mar"  /  "Apr"
    //                 /  "May"  /  "Jun" /  "Jul"  /  "Aug"
    //                 /  "Sep"  /  "Oct" /  "Nov"  /  "Dec"
    //
    //     time        =  hour zone                    ; ANSI and Military
    //
    //     hour        =  2DIGIT ":" 2DIGIT [":" 2DIGIT]
    //                                                 ; 00:00:00 - 23:59:59
    //
    //     zone        =  "UT"  / "GMT"                ; Universal Time
    //                                                 ; North American : UT
    //                 /  "EST" / "EDT"                ;  Eastern:  - 5/ - 4
    //                 /  "CST" / "CDT"                ;  Central:  - 6/ - 5
    //                 /  "MST" / "MDT"                ;  Mountain: - 7/ - 6
    //                 /  "PST" / "PDT"                ;  Pacific:  - 8/ - 7
    //                 /  1ALPHA                       ; Military: Z = UT;
    //                                                 ;  A:-1; (J not used)
    //                                                 ;  M:-12; N:+1; Y:+12
    //                 / ( ("+" / "-") 4DIGIT )        ; Local differential
    //                                                 ;  hours+min. (HHMM)
    val rfcDay = this.dayOfWeek.toRFC822()
    val rfcDate = this.date.toRFC822()
    val rfcTime = this.toRfC822Time()

    val rfcZone = this.toRFC822Zone()
    return "$rfcDay, $rfcDate $rfcTime $rfcZone"
}

/**
 * Formats into date-time format defined in RFC3501 (IMAP4rev1). Looks like:
 *
 * "17-Jul-1996 02:44:25 -0700"
 *
 * (including quotes)
 */
fun LocalDateTime.toRFC3501(): String {
    //     date            = date-text / DQUOTE date-text DQUOTE
    //
    //     date-day        = 1*2DIGIT
    //                         ; Day of month
    //
    //     date-day-fixed  = (SP DIGIT) / 2DIGIT
    //                         ; Fixed-format version of date-day
    //
    //     date-month      = "Jan" / "Feb" / "Mar" / "Apr" / "May" / "Jun" /
    //                       "Jul" / "Aug" / "Sep" / "Oct" / "Nov" / "Dec"
    //
    //     date-text       = date-day "-" date-month "-" date-year
    //
    //
    //
    //
    //     Crispin                     Standards Track                    [Page 84]
    //
    //     date-year       = 4DIGIT
    //
    //     date-time       = DQUOTE date-day-fixed "-" date-month "-" date-year
    //                  SP time SP zone DQUOTE

    val rfcDate = "${this.dayOfMonth}-${this.month.toRFC822()}-${this.year}"
    return "\"$rfcDate ${this.toRfC822Time()} ${this.toRFC822Zone()}\""
}

private fun DayOfWeek.toRFC822(): String = when (this) {
    DayOfWeek.MONDAY -> "Mon"
    DayOfWeek.TUESDAY -> "Tue"
    DayOfWeek.WEDNESDAY -> "Wed"
    DayOfWeek.THURSDAY -> "Thu"
    DayOfWeek.FRIDAY -> "Fri"
    DayOfWeek.SATURDAY -> "Sat"
    DayOfWeek.SUNDAY -> "Sun"
}

private fun LocalDate.toRFC822(): String =
    "${this.dayOfMonth} ${this.month.toRFC822()} ${this.year}"

@SharedImmutable
private val monthNames = listOf(
    "Jan", "Feb", "Mar", "Apr",
    "May", "Jun", "Jul", "Aug",
    "Sep", "Oct", "Nov", "Dec"
)

fun monthFromName(name: String): Month? {
    val index = monthNames.indexOf(name)
    return if (index == -1) {
        null
    } else {
        return Month(index + 1)
    }
}

private fun Month.toRFC822(): String = monthNames[this.number - 1]

private fun LocalDateTime.toRfC822Time(): String =
    "${this.hour.padTwo()}:${this.minute.padTwo()}:${this.second.padTwo()}"

private fun Int.padTwo(): String = this.toString().padStart(2, '0')

private fun LocalDateTime.toRFC822Zone(): String {
    val zone = TimeZone.currentSystemDefault()
    val offsetSeconds = zone.offsetAt(this.toInstant(zone)).totalSeconds
    val offsetHours = offsetSeconds / 3600
    val offsetMinutes = (offsetSeconds % 3600) / 60
    val sign = if (offsetSeconds > 0) '+' else '-'
    return "$sign${offsetHours.padTwo()}${offsetMinutes.padTwo()}"
}