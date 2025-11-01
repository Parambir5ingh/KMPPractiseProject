package org.prm.drica

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import org.prm.drica.addnew.AddNewViewModel
import org.prm.drica.db.DriCaDatabase
import org.prm.drica.home.TransactionLogs
import org.prm.drica.ui.TitleBar
import org.prm.drica.ui.theme.ScreenBackgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(database: DriCaDatabase) {
    val viewModel = remember { AddNewViewModel(database) }

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

            TitleBar("DriCA")

            Box(modifier = Modifier.fillMaxSize()) {

                TransactionLogs(database)

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