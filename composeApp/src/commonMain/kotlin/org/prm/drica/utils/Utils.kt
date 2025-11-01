package org.prm.drica.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/*
* Created by parambirsingh ON 27/10/25
*/

fun Double.roundToDecimals(decimals: Int): Float {
    var dotAt = 1
    repeat(decimals) { dotAt *= 10 }
    val roundedValue = (this * dotAt).roundToInt()
    return (roundedValue / dotAt) + (roundedValue % dotAt).toFloat() / dotAt
}

fun Double.toValidString(): String {
    return if (this == 0.0) "" else this.toString()
}

@OptIn(ExperimentalTime::class)
fun formatDateTime(epochMillis: Long): String {
    val instant = Instant.fromEpochMilliseconds(epochMillis)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    // Example: format like "27 Oct 2025, 3:45 PM"
    val day = localDateTime.dayOfMonth
    val month = localDateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    val year = localDateTime.year

    val hour = localDateTime.hour
    val minute = localDateTime.minute
    val amPm = if (hour < 12) "AM" else "PM"
    val displayHour = if (hour % 12 == 0) 12 else hour % 12

    return "${day.toString().padStart(2, '0')} $month $year, " +
            "${displayHour}:${minute.toString().padStart(2, '0')} $amPm"
}
