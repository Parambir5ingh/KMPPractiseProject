package org.prm.drica.navigation.home.dashboard

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.prm.drica.models.TransactionDataModel
import org.prm.drica.utils.calculateMileagePerLitreFromGasFills
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Pre-computed dashboard metrics for a single calendar month.
 * The UI renders one [MonthDashboardSummary] per card in the monthly list.
 */
data class MonthDashboardSummary(
    val year: Int,
    val month: Month,
    val label: String,
    val startMillis: Long,
    val endMillis: Long,
    val totalEarnings: Double,
    val totalProfit: Double,
    val kmDriven: Long,
    val mileagePerLitre: Double,
)

/**
 * Builds the full dashboard list from raw transactions.
 *
 * Flow:
 * 1. Discover every (year, month) that has at least one transaction.
 * 2. Sort months newest-first for the LazyColumn.
 * 3. For each month, compute earnings, profit, km driven, and fuel mileage.
 *
 * Gas fills are passed separately because mileage uses tank-to-tank logic
 * (see [calculateMileagePerLitreFromGasFills] in Utils).
 */
@OptIn(ExperimentalTime::class)
fun buildMonthSummaries(
    transactions: List<TransactionDataModel>,
    gasFills: List<TransactionDataModel>,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): List<MonthDashboardSummary> {
    if (transactions.isEmpty()) return emptyList()

    // Only months with data appear; no empty future/past months.
    val monthKeys = transactions
        .map { Instant.fromEpochMilliseconds(it.dateTime).toLocalDateTime(timeZone).date }
        .map { it.year to it.month }
        .distinct()
        .sortedWith(compareByDescending<Pair<Int, Month>> { it.first }.thenByDescending { it.second.ordinal })

    return monthKeys.map { (year, month) ->
        // Half-open range [startMillis, endMillis) = first instant of month through last instant before next month.
        val startMillis = LocalDate(year, month, 1).atStartOfDayIn(timeZone).toEpochMilliseconds()
        val endMillis = LocalDate(year, month, 1)
            .plus(1, DateTimeUnit.MONTH)
            .atStartOfDayIn(timeZone)
            .toEpochMilliseconds()

        val inPeriod = transactions.filter { it.dateTime in startMillis..<endMillis }
        // Earnings = income only (positive amounts). Profit = net of income and expenses (signed sum).
        val totalEarnings = inPeriod.filter { it.amount > 0 }.sumOf { it.amount }
        val totalProfit = inPeriod.sumOf { it.amount }
        val kmDriven = kmDrivenInPeriod(transactions, startMillis, endMillis)
        val mileagePerLitre = calculateMileagePerLitreFromGasFills(gasFills, startMillis, endMillis)

        MonthDashboardSummary(
            year = year,
            month = month,
            label = formatMonthYearLabel(month, year),
            startMillis = startMillis,
            endMillis = endMillis,
            totalEarnings = totalEarnings,
            totalProfit = totalProfit,
            kmDriven = kmDriven,
            mileagePerLitre = mileagePerLitre,
        )
    }
}

/**
 * Odometer-based distance for the month: highest minus lowest [totalKms] among
 * entries in the period (not trip-by-trip). Uses all transaction types that log odometer.
 */
private fun kmDrivenInPeriod(
    transactions: List<TransactionDataModel>,
    start: Long,
    end: Long,
): Long {
    val inRange = transactions
        .filter { it.dateTime in start..<end && it.totalKms > 0 }
        .sortedBy { it.dateTime }
    if (inRange.isEmpty()) return 0L
    val firstKm = inRange.first().totalKms
    val lastKm = inRange.maxOf { it.totalKms }
    return (lastKm - firstKm).toLong().coerceAtLeast(0L)
}

private fun formatMonthYearLabel(month: Month, year: Int): String {
    val monthName = month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    return "$monthName $year"
}

/** Income for the month divided by [kmDriven]; shown only when km > 0. */
fun calculateEarningsPerKm(totalEarnings: Double, totalKm: Double): Double =
    if (totalKm > 0.0) totalEarnings / totalKm else 0.0
