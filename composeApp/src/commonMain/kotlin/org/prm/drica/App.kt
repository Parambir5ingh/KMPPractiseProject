package org.prm.drica

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import drica.composeapp.generated.resources.Res
import drica.composeapp.generated.resources.ic_add
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.prm.drica.db.TransactionsDao
import org.prm.drica.ui.theme.ScreenBackgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(transactionDao: TransactionsDao) {
    MaterialTheme {
        val dataList by transactionDao.getAll().collectAsState(initial = emptyList())
        val scope = rememberCoroutineScope()

        LaunchedEffect(true) {
            val transactionList = listOf<TransactionDataModel>(
                TransactionDataModel(0, "Expense", "Gas Refueled"),
                TransactionDataModel(0, "Expense", "Wipers changed"),
                TransactionDataModel(0, "Income", "Deliveries income"),
                TransactionDataModel(0, "Income", "Insurance Claim"),
            )
            transactionList.forEach { transactionDao.upsert(it) }
        }

        var showAddNewScreen by remember { mutableStateOf(false) }
//        val dataList = remember { mutableStateListOf<String>() }
        val sheetState = rememberModalBottomSheetState()

        Column(
            modifier = Modifier
                .background(ScreenBackgroundColor)
                .systemBarsPadding()
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets(0)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            TitleBar("DriCA")

            Box(modifier = Modifier.fillMaxSize()) {

                HomeList(dataList)

                if (showAddNewScreen) {
                    ModalBottomSheet(
                        onDismissRequest = { showAddNewScreen = false },
                        sheetState = sheetState
                    ) {
                        AddNew(onDismissed = {
                            showAddNewScreen = false
                        })
                    }
                }

                // ADD button
                FloatingActionButton(
                    onClick = {
//                        dataList.add(0, "Item #${dataList.size + 1}")
                        showAddNewScreen = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(25.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_add),
                        contentDescription = "Add",
                    )
                }
            }
        }
    }
}