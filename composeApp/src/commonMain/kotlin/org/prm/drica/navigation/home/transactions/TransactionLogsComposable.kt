package org.prm.drica.navigation.home.transactions

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.prm.drica.db.DriCaDatabase
import org.prm.drica.models.TransactionDataModel
import org.prm.drica.ui.theme.DarkGreen
import org.prm.drica.ui.theme.Red
import org.prm.drica.ui.GenericOptionsMenu
import org.prm.drica.ui.MenuItem
import org.prm.drica.utils.formatDate
import org.prm.drica.utils.roundToDecimals

@Composable
fun TransactionLogs(database: DriCaDatabase) {
    var viewModel = remember { TransLogsViewModel(database) }

    val dataList by viewModel.transactionDao.getAll().collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize().animateContentSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(dataList) { item ->
            TransactionCard(item, { deletedModel ->
                viewModel.deleteEntry(deletedModel)
            })
        }
    }
}

@Preview
@Composable
fun PrevTransactionCard() {
    TransactionCard(TransactionDataModel(0, "Income", "DoorDash", 500.0, 30000.0, 60000.0, "Test Notes", 0, false, "", 0), {})
}

@Composable
fun TransactionCard(tx: TransactionDataModel, onDeletion: (TransactionDataModel) -> Unit) {
    val color = if (tx.type.equals("Income")) DarkGreen else Red
    val label = tx.category
    val totalKilometers = tx.totalKms
    val notes = tx.notes
    val dateText = remember(tx.dateTime) { formatDate(tx.dateTime) }

    Card(
        modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp), shape = MaterialTheme.shapes.medium, elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().background(Color.White).padding(start = 16.dp, top = 16.dp, bottom = 16.dp).weight(1f),
                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label, color = color, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    if (notes.trim().isNotEmpty()) {
                        Text(
                            text = notes, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Normal)
                        )
                    }
                    Text(
                        text = "$totalKilometers Km", color = Black, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Column(modifier = Modifier.wrapContentWidth(), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = (if (tx.type.equals("Income")) "+$" else "-$") + tx.amount.roundToDecimals(2),
                        color = Black,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = dateText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.End
                    )
                }
            }

            val menuItems = ArrayList<MenuItem>()
//            menuItems.add(MenuItem("Edit", {}))
            menuItems.add(MenuItem("Delete", {
                onDeletion(tx)
            }))
            GenericOptionsMenu(menuItems)
        }
    }
}

