package org.prm.drica.navigation.home.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
    val totalEarnings by viewModel.totalEarnings.collectAsState()
    val totalEarningsThisMonth by viewModel.totalEarningsThisMonth.collectAsState()
    val totalProfit by viewModel.totalProfit.collectAsState()
    val totalProfitThisMonth by viewModel.totalProfitThisMonth.collectAsState()
    val totalProfitLastMonth by viewModel.totalProfitLastMonth.collectAsState()
    val totalEarningsLastMonth by viewModel.totalEarningsLastMonth.collectAsState()
    val kmDriveThisMonth by viewModel.kmDriveThisMonth.collectAsState()
    val kmDriveLastMonth by viewModel.kmDriveLastMonth.collectAsState()
    val mileagePerLitreThisMonth by viewModel.mileagePerLitreThisMonth.collectAsState()
    val mileagePerLitreLastMonth by viewModel.mileagePerLitreLastMonth.collectAsState()
    val daysWorked by viewModel.daysWorked.collectAsState()
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(dataList) {
        viewModel.loadData()

        val data = viewModel.getLineChartData()

        if (data.isNotEmpty()) {
            modelProducer.runTransaction {
                lineSeries {
                    data.forEach { seriesPoints ->
                        if (seriesPoints.isNotEmpty()) {
                            series(seriesPoints.map { it.y })
                        }
                    }
                }
            }
        }
    }

    DashboardScreen(
        modelProducer,
        viewModel,
        totalEarnings,
        totalEarningsThisMonth,
        totalProfit,
        totalProfitThisMonth,
        totalEarningsLastMonth,
        totalProfitLastMonth,
        kmDriveThisMonth,
        kmDriveLastMonth,
        mileagePerLitreThisMonth,
        mileagePerLitreLastMonth
    )
}

@Preview
@Composable
fun PrevHomeComposable() {
//    DashboardScreen(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0)
}

@Composable
fun DashboardScreen(
    chartModelProducer: CartesianChartModelProducer,
    viewModel: DashboardViewModel,
    earnings: Double,
    totalEarningsThisMonth: Double,
    totalProfit: Double,
    totalProfitThisMonth: Double,
    totalEarningsLastMonth: Double,
    totalProfitLastMonth: Double,
    kmDriveThisMonth: Long,
    kmDriveLastMonth: Long,
    mileagePerLitreThisMonth: Double,
    mileagePerLitreLastMonth: Double
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
//        ComposeBasicLineChart(chartModelProducer, Modifier.fillMaxWidth().wrapContentHeight(),)

        SummaryCard(
            viewModel,
            "This Month",
            totalEarningsThisMonth,
            totalProfitThisMonth,
            kmDriveThisMonth,
            mileagePerLitreThisMonth
        )

        Spacer(modifier = Modifier.height(16.dp))
        SummaryCard(
            viewModel,
            "Last Month",
            totalEarningsLastMonth,
            totalProfitLastMonth,
            kmDriveLastMonth,
            mileagePerLitreLastMonth
        )

        Spacer(modifier = Modifier.height(16.dp))
        SummaryCard(viewModel, "Overall", earnings, totalProfit, 0, 0.0)
    }
}

@Composable
fun SummaryCard(
    viewModel: DashboardViewModel,
    label: String,
    totalEarnings: Double,
    totalProfit: Double,
    kmDriven: Long?,
    mileagePerLitre: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
        ) {

            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = label,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            var roundedEarnings = totalEarnings.roundToDecimals(2)
            var formattedEarnings = if (totalEarnings < 0) "-$$roundedEarnings" else "$$roundedEarnings"
            SummaryRow(
                label = "Total Earnings",
                value = formattedEarnings
            )

            Spacer(modifier = Modifier.height(12.dp))

            var roundedProfit = totalProfit.roundToDecimals(2)
            var formattedTotalProfit = if (totalProfit < 0) "-$$roundedProfit" else "$$roundedProfit"
            SummaryRow(
                label = "Total Profit",
                value = formattedTotalProfit,
                valueColor = MaterialTheme.colorScheme.primary
            )

            if (kmDriven != 0L) {
                Spacer(modifier = Modifier.height(12.dp))

                SummaryRow(
                    label = "Km Driven",
                    value = kmDriven?.let { "$it km" } ?: "--"
                )
            }

            kmDriven?.toDouble()?.let { km ->
                val earningPerKM = viewModel.calculateEarningsPerKm(totalEarnings, km)
                if (earningPerKM != 0.0) {
                    Spacer(modifier = Modifier.height(12.dp))

                    SummaryRow(
                        label = "Earnings Per KM",
                        value = "$${earningPerKM.roundToDecimals(2)}"
                    )
                }
            }

            if (mileagePerLitre != 0.0) {
                Spacer(modifier = Modifier.height(12.dp))

                SummaryRow(
                    label = "Mileage Per Litre",
                    value = "${mileagePerLitre.roundToDecimals(2)} km/L"
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}
