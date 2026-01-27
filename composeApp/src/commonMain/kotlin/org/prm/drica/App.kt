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
import androidx.compose.foundation.pager.PagerState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import drica.composeapp.generated.resources.Res
import drica.composeapp.generated.resources.ic_add
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.prm.drica.navigation.addnew.AddNew
import org.prm.drica.navigation.addnew.AddTransactionViewModel
import org.prm.drica.db.DriCaDatabase
import org.prm.drica.navigation.home.Tabs
import org.prm.drica.navigation.home.dashboard.DashboardComposable
import org.prm.drica.navigation.home.transactions.TransactionLogs
import org.prm.drica.navigation.Screen
import org.prm.drica.navigation.settings.ExportAppData
import org.prm.drica.navigation.settings.SettingsScreen
import org.prm.drica.ui.TitleBar
import org.prm.drica.ui.theme.ScreenBackgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(database: DriCaDatabase) {
    val addTransViewModel = remember { AddTransactionViewModel(database) }
    val appViewModel = remember { AppViewModel(database) }

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { Tabs.entries.size })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    val transactionsData by appViewModel.transactionsData.collectAsState()

    val navController = rememberNavController()

    LaunchedEffect(transactionsData) {
        transactionsData?.let { data ->
            ExportAppData.exportAppData(data)
            appViewModel.onExportDone()
        }
    }

    MaterialTheme {
        val scope = rememberCoroutineScope()

        var showAddNewScreen by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState()

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) {
                HomeComposable(
                    onNavigate = {
                        navController.navigate(Screen.Settings.route)
                    },
                    selectedTabIndex, scope, pagerState, database, { showAddNewScreen = true }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBackPressed = {
                        navController.popBackStack()
                    },
                    onExportDataClicked = {
                        scope.launch {
                            appViewModel.onExportClicked()
                        }
                    }
                )
            }
        }

        if (showAddNewScreen) {
            ModalBottomSheet(
                onDismissRequest = { showAddNewScreen = false },
                sheetState = sheetState,
                containerColor = Color.White,
            ) {
                AddNew(onDismissed = {
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        showAddNewScreen = false
                    }
                }, addTransViewModel)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HomeComposable(
    onNavigate: () -> Unit,
    selectedTabIndex: State<Int>,
    scope: CoroutineScope,
    pagerState: PagerState,
    database: DriCaDatabase,
    onAddButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(ScreenBackgroundColor)
            .systemBarsPadding()
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets(0)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        TitleBar(
            "DriCA", {
                onNavigate()
            },
            null
        )

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
                        .weight(1f),
                    userScrollEnabled = true,
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

            // ADD button
            FloatingActionButton(
                onClick = {
                    println("add new button clicked")
                    onAddButtonClick()
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