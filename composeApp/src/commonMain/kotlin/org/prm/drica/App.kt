package org.prm.drica

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.prm.drica.addnew.AddNew
import org.prm.drica.addnew.AddTransactionViewModel
import org.prm.drica.db.DriCaDatabase
import org.prm.drica.home.Tabs
import org.prm.drica.home.dashboard.DashboardComposable
import org.prm.drica.home.transactions.TransactionLogs
import org.prm.drica.ui.TitleBar
import org.prm.drica.ui.theme.ScreenBackgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(database: DriCaDatabase) {
    val viewModel = remember { AddTransactionViewModel(database) }

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { Tabs.entries.size })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    MaterialTheme {
        val scope = rememberCoroutineScope()

        var showAddNewScreen by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState()

        Column(
            modifier = Modifier
                .background(ScreenBackgroundColor)
                .systemBarsPadding()
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets(0)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            TitleBar("DriCA", {

            })

            Box(modifier = Modifier.fillMaxSize()) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
//                        .padding(top = it.calculateTopPadding())
                ) {
                    TabRow(
//                        backgroundColor = Color.Transparent,
                        selectedTabIndex = selectedTabIndex.value,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Tabs.entries.forEachIndexed { index, currentTab ->
                            Tab(
                                selected = selectedTabIndex.value == index,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(currentTab.ordinal)
                                    }
                                },
                                text = { Text(text = currentTab.text) }
                            )
                        }
                    }
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            when (selectedTabIndex.value) {
                                0 -> DashboardComposable(database)
                                1 -> TransactionLogs(database)
                                else -> Text(text = Tabs.entries[selectedTabIndex.value].text)
                            }
                        }
                    }
                }

                if (showAddNewScreen) {
                    ModalBottomSheet(
                        onDismissRequest = { showAddNewScreen = false },
                        sheetState = sheetState
                    ) {
                        AddNew(onDismissed = {
                            scope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                showAddNewScreen = false
                            }
                        }, viewModel)
                    }
                }

                // ADD button
                FloatingActionButton(
                    onClick = {
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