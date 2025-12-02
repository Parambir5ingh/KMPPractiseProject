package org.prm.drica.home.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.prm.drica.db.DriCaDatabase

@Composable
fun DashboardComposable(database: DriCaDatabase) {
    val viewModel = remember { DashboardViewModel(database) }
    val dataList by viewModel.transactionDao.getAll().collectAsState(initial = emptyList())
    val totalEarnings by viewModel.totalEarnings.collectAsState()
    val totalProfit by viewModel.totalProfit.collectAsState()
    val daysWorked by viewModel.daysWorked.collectAsState()

    LaunchedEffect(dataList) {
        viewModel.loadData()
    }

    DashboardScreen(totalEarnings, daysWorked, totalProfit)
}

@Preview
@Composable
fun PrevHomeComposable() {
    DashboardScreen(0.0, 0, 0.0)
}

@Composable
fun DashboardScreen(earnings: Double, daysWorked: Int, totalProfit: Double) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DashboardCard(
                modifier = Modifier
                    .weight(1f),
                title = "$$earnings\nTotal Earnings",
                background = Color(0xFFB0BEC5)
            )

            DashboardCard(
                modifier = Modifier
                    .weight(1f),
                title = "In\n$daysWorked Days",
                background = Color(0xFFE57373)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            DashboardCard(
                modifier = Modifier
                    .weight(1f),
                title = "$$totalProfit\nTotal Profit",
                background = Color(0xFF81C784)
            )

        }
    }
}

@Composable
fun DashboardCard(modifier: Modifier, title: String, background: Color) {
    Card(
        modifier = modifier
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}
