package org.prm.drica.navigation.home.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.prm.drica.db.DriCaDatabase
import org.prm.drica.utils.roundToDecimals

@Composable
fun DashboardComposable(database: DriCaDatabase) {
    val viewModel = remember { DashboardViewModel(database) }
    val dataList by viewModel.transactionDao.getAll().collectAsState(initial = emptyList())
    val monthSummaries by viewModel.monthSummaries.collectAsState()
    val modelProducer = remember { CartesianChartModelProducer() }

    // Rebuild monthly summaries whenever Room emits an updated transaction list.
    LaunchedEffect(dataList) {
        viewModel.loadData(dataList)

        val chartData = viewModel.getLineChartData()
        if (chartData.any { it.isNotEmpty() }) {
            modelProducer.runTransaction {
                lineSeries {
                    chartData.forEach { seriesPoints ->
                        if (seriesPoints.isNotEmpty()) {
                            series(seriesPoints.map { it.y })
                        }
                    }
                }
            }
        }
    }

    DashboardScreen(monthSummaries = monthSummaries)
}

@Preview
@Composable
fun PrevHomeComposable() {
    DashboardScreen(monthSummaries = emptyList())
}

/**
 * One scrollable list of month cards (newest at top).
 * Replaces the previous fixed "This month / Last month / Overall" layout.
 */
@Composable
fun DashboardScreen(monthSummaries: List<MonthDashboardSummary>) {
    if (monthSummaries.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "No transactions yet.\nAdd entries to see monthly summaries.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Stable keys avoid unnecessary recomposition when the list is refreshed.
        items(
            items = monthSummaries,
            key = { "${it.year}-${it.month}" },
        ) { summary ->
            SummaryCard(summary = summary)
        }
    }
}

/** Renders pre-computed [MonthDashboardSummary] metrics; optional rows hidden when value is zero. */
@Composable
fun SummaryCard(
    summary: MonthDashboardSummary,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp),
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = summary.label,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            val totalEarnings = summary.totalEarnings
            val roundedEarnings = totalEarnings.roundToDecimals(2)
            val formattedEarnings =
                if (totalEarnings < 0) "-$$roundedEarnings" else "$$roundedEarnings"
            SummaryRow(label = "Total Earnings", value = formattedEarnings)

            Spacer(modifier = Modifier.height(12.dp))

            val totalProfit = summary.totalProfit
            val roundedProfit = totalProfit.roundToDecimals(2)
            val formattedTotalProfit =
                if (totalProfit < 0) "-$$roundedProfit" else "$$roundedProfit"
            SummaryRow(
                label = "Total Profit",
                value = formattedTotalProfit,
                valueColor = MaterialTheme.colorScheme.primary,
            )

            val kmDriven = summary.kmDriven
            if (kmDriven != 0L) {
                Spacer(modifier = Modifier.height(12.dp))
                SummaryRow(label = "Km Driven", value = "$kmDriven km")
            }

            // Derived in UI from earnings ÷ km; mileage comes from gas fill-ups in [buildMonthSummaries].
            if (kmDriven != 0L) {
                val earningPerKm = calculateEarningsPerKm(totalEarnings, kmDriven.toDouble())
                if (earningPerKm != 0.0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    SummaryRow(
                        label = "Earnings Per KM",
                        value = "$${earningPerKm.roundToDecimals(2)}",
                    )
                }
            }

            val mileagePerLitre = summary.mileagePerLitre
            if (mileagePerLitre != 0.0) {
                Spacer(modifier = Modifier.height(12.dp))
                SummaryRow(
                    label = "Mileage Per Litre",
                    value = "${mileagePerLitre.roundToDecimals(2)} km/L",
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
        )
    }
}
