package org.prm.drica.navigation.settings.Vehicles

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.calf.core.LocalPlatformContext
import drica.composeapp.generated.resources.Res
import drica.composeapp.generated.resources.ic_add
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.prm.drica.db.DriCaDatabase
import org.prm.drica.navigation.settings.Vehicles.AddVehicle.AddVehicleBottomSheet
import org.prm.drica.ui.TitleBar

@Composable
@Preview
fun VehicleListPreview() {
//    VehicleListScreen(null,{})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleListScreen(
    database: DriCaDatabase,
    onBackPressed: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalPlatformContext.current

    var vehicleViewModel = remember { VehicleViewModel(database) }
    val dataList by vehicleViewModel.vehicleDao.getAll().collectAsState(initial = emptyList())
    var showAddNewScreen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding().background(Color.White)
    ) {

        // 🔹 Title Bar
        TitleBar(
            title = "Vehicles", null, onBackPressed = {
                onBackPressed()
            })

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            if (dataList.isEmpty()) {
                Text(
                    "No Data Found", fontSize = 18.sp
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().animateContentSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(dataList) { item ->
                        VehicleCard(
                            vehicle = item,
                            onClick = {
                                // handle click if needed
                            }
                        )
                    }
                }
            }

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

    if (showAddNewScreen) {
        ModalBottomSheet(
            onDismissRequest = { showAddNewScreen = false },
            sheetState = sheetState,
            containerColor = Color.White,
        ) {
            AddVehicleBottomSheet(
                viewModel = vehicleViewModel,
                onSaved = {
                    showAddNewScreen = false
                }
            )
        }
    }
}
