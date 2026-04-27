package org.prm.drica.navigation.settings.Vehicles.AddVehicle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.prm.drica.navigation.settings.Vehicles.VehicleViewModel
import org.prm.drica.ui.WheelDatePickerBottomSheet
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleBottomSheet(
    viewModel: VehicleViewModel,
    onSaved: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val vehicleTypes = listOf(
        "NA",
        "Hatchback",
        "Sedan",
        "SUV",
        "Truck",
        "VAN",
        "Semi-Truck",
        "Bus"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Add New Vehicle",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = viewModel.name,
            onValueChange = { viewModel.name = it },
            label = { Text("Vehicle Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // TYPE DROPDOWN
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {

            OutlinedTextField(
                value = viewModel.type,
                onValueChange = {},
                readOnly = true,
                label = { Text("Vehicle Type") },
                trailingIcon = {
                    TrailingIcon(expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {

                vehicleTypes.forEach {

                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            viewModel.type = it
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = viewModel.year,
            onValueChange = { viewModel.year = it },
            label = { Text("Year") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.kms,
            onValueChange = { viewModel.kms = it },
            label = { Text("Kilometers") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
        ) {

            OutlinedTextField(
                value = viewModel.boughtOn,
                onValueChange = {},
                enabled = false,
                label = { Text("Purchase Date") },
                placeholder = { Text("Select Date") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select Date"
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        WheelDatePickerBottomSheet(
            show = showDatePicker,
            selectedDate = Clock.System.now().toEpochMilliseconds(),
            onDone = { date, dateTimeMilis ->
                showDatePicker = false
                viewModel.boughtOn = date
            },
            onDismiss = { showDatePicker = false }
        )

        viewModel.error?.let {
            Text(
                text = it,
                color = Color.Red
            )
        }

        Button(
            onClick = {
                viewModel.saveVehicle(onSaved)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isSaving
        ) {

            if (viewModel.isSaving)
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
            else
                Text("Save Vehicle")
        }
    }
}