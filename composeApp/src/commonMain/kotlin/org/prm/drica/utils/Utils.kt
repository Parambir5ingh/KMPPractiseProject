package org.prm.drica.utils

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import org.prm.drica.models.TransactionDataModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/*
* Created by parambirsingh ON 27/10/25
*/

fun Double.roundToDecimals(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return round(this * factor) / factor
}

fun Double.toValidDecimalString(): String {
    return if (this == 0.0) "" else this.toString()
}

fun Double.toValidLongString(): String {
    return if (this == 0.0) "" else this.roundToLong().toString()
}

@OptIn(ExperimentalTime::class)
fun formatDate(epochMillis: Long): String {
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

    return "${day.toString().padStart(2, '0')} $month $year"
}

fun getCurrentMonthRange(): Pair<Long, Long> {
    val now = Clock.System.now()
    val timeZone = TimeZone.currentSystemDefault()
    val today = now.toLocalDateTime(timeZone).date

    val startOfMonth = LocalDate(today.year, today.month, 1)
        .atStartOfDayIn(timeZone)
        .toEpochMilliseconds()

    val startOfNextMonth = startOfMonth
        .let {
            val nextMonthDate = startOfMonth
                .let { Instant.fromEpochMilliseconds(it) }
                .toLocalDateTime(timeZone)
                .date
                .plus(1, DateTimeUnit.MONTH)

            nextMonthDate
                .atStartOfDayIn(timeZone)
                .toEpochMilliseconds()
        }

    return Pair(startOfMonth, startOfNextMonth)
}

fun getLastMonthRange(): Pair<Long, Long> {
    val now = Clock.System.now()
    val timeZone = TimeZone.currentSystemDefault()
    val today = now.toLocalDateTime(timeZone).date

    // Start of this month
    val startOfThisMonth = LocalDate(today.year, today.month, 1)

    // Start of last month
    val startOfLastMonth = startOfThisMonth.minus(DatePeriod(months = 1))

    // End of last month = start of this month
    val endOfLastMonth = startOfThisMonth

    val startMillis = startOfLastMonth
        .atStartOfDayIn(timeZone)
        .toEpochMilliseconds()

    val endMillis = endOfLastMonth
        .atStartOfDayIn(timeZone)
        .toEpochMilliseconds()

    return startMillis to endMillis
}

/**
 * Tank-to-tank mileage for a period: km between consecutive gas fill-ups divided by
 * litres purchased on fills that fall within [start, end). Grouped per vehicle.
 */
fun calculateMileagePerLitreFromGasFills(
    gasFills: List<TransactionDataModel>,
    start: Long,
    end: Long,
): Double {
    var totalKm = 0.0
    var totalLitres = 0.0

    gasFills.groupBy { it.vehicleId }.values.forEach { vehicleFills ->
        var previousFill: TransactionDataModel? = null
        vehicleFills.sortedBy { it.dateTime }.forEach { fill ->
            val litres = abs(fill.amount) / fill.fuelPrice
            if (litres <= 0.0) return@forEach

            if (fill.dateTime in start..<end) {
                totalLitres += litres
                val previous = previousFill
                if (previous != null && fill.totalKms > previous.totalKms) {
                    totalKm += fill.totalKms - previous.totalKms
                }
            }
            previousFill = fill
        }
    }

    return if (totalKm > 0.0 && totalLitres > 0.0) totalKm / totalLitres else 0.0
}