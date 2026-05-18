package org.prm.drica.navigation.addnew

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.prm.drica.ui.Dropdown
import org.prm.drica.ui.WheelDatePickerBottomSheet
import org.prm.drica.utils.formatDate
import org.prm.drica.utils.toValidDecimalString
import org.prm.drica.utils.toValidLongString
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
@Preview
fun PreviewAddTransactionComoposable() {
//    AddTransactionComoposable({}, AddNewViewModel())
}

@OptIn(ExperimentalTime::class)
@Composable
fun AddTransactionComoposable(
    onDismissed: () -> Unit,
    viewModel: AddTransactionViewModel,
    sheetVisible: Boolean = true,
) {
//    var transactionData by remember { mutableStateOf<TransactionDataModel?>(null) }
    val transactionState by viewModel.transactionState.collectAsState()
    val amountError by viewModel.amountError.collectAsState()
    val totalKmsError by viewModel.totalKmsError.collectAsState()
    val isValid by viewModel.isValid.collectAsState()

    val entryTypeOptions by viewModel.entryTypeOptions.collectAsState()
    val vehicleList by viewModel.vehicleList.collectAsState()
    val expenseTypeOptions by viewModel.expenseTypeOptions.collectAsState()
    val incomeTypeOptions by viewModel.incomeTypeOptions.collectAsState()
    val selectedEntryType by viewModel.selectedEntryType.collectAsState()
    val selectedVehicle by viewModel.selectedVehicle.collectAsState()
    val selectedExpenseType by viewModel.selectedExpenseType.collectAsState()
    val selectedIncomeType by viewModel.selectedIncomeType.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(selectedVehicle) {
        if (selectedVehicle != null) {
            viewModel.vehiclesDao.getVehicleById(selectedVehicle?.id.toString())?.let {
                viewModel.onTotalKmChanged(it.kms.toDouble())
            } ?: run {
                viewModel.onTotalKmChanged(0.00)
            }
        } else {
            viewModel.transactionDao.getLastTransaction()?.let {
                viewModel.onTotalKmChanged(it.totalKms)
            }
        }
    }

    // Re-run when the sheet opens so defaults and fuel price load reliably on Android.
    LaunchedEffect(sheetVisible) {
        if (!sheetVisible) return@LaunchedEffect
        viewModel.prepareNewTransaction()
        viewModel.onSelectedDate("", Clock.System.now().toEpochMilliseconds())
    }

    Column(
        modifier = Modifier.fillMaxWidth().background(Color.White).verticalScroll(rememberScrollState()).imePadding(),
//        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Column(
            modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = if (transactionState.dateTime == 0L) "Select Date" else formatDate(transactionState.dateTime),
                modifier = Modifier
//                    .padding(4.dp)
                    .fillMaxWidth()
//                    .background(Color.LightGray.copy(alpha = 0.2f))
//                    .padding(4.dp)
                    .clickable { showDatePicker = true },
                textAlign = TextAlign.Center
            )

            WheelDatePickerBottomSheet(
                show = showDatePicker,
                selectedDate = transactionState.dateTime,
                onDone = { date, dateTimeMilis ->
                    showDatePicker = false
                    viewModel.onSelectedDate(date, dateTimeMilis)
                },
                onDismiss = { showDatePicker = false }
            )
            Row(modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth()) {
                if (vehicleList.isNotEmpty()) {
                    Box(modifier = Modifier.weight(1f)) {
                        val selectedVehicleName = selectedVehicle?.name ?: ""
                        VehiclesDropdown("Vehicle", vehicleList, selectedValue = selectedVehicleName, onSelectOption = {
                            viewModel.onSelectedVehicle(it)
                        })
                    }
                    Spacer(modifier = Modifier.width(5.dp).fillMaxHeight())
                }
                Box(modifier = Modifier.weight(1f)) {
                    Dropdown("Entry Type", entryTypeOptions, selectedValue = selectedEntryType, onSelectOption = {
                        viewModel.onSelectedEntryType(it)
                    })
                }
            }
            Row(modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth()) {
                Box(modifier = Modifier.weight(2f)) {
                    if (selectedEntryType == entryTypeOptions[0]) { // Income
                        Dropdown(
                            "Income Type",
                            incomeTypeOptions,
                            selectedValue = selectedIncomeType,
                            onSelectOption = { viewModel.onSelectedCategoryType(it) }
                        )
                    } else { // Expense
                        Dropdown(
                            "Expense Type",
                            expenseTypeOptions,
                            selectedValue = selectedExpenseType,
                            onSelectOption = { viewModel.onSelectedCategoryType(it) }
                        )
                    }
                }

                // Price/L field only for Gas expenses; visibility follows selected type/category.
                if (selectedEntryType == "Expense" && selectedExpenseType == "Gas") {
//                    HJE VEHICLE LIST POPULATE KRKE OHDIYA ENTRIES DROPDOWN CH SELECT KRAUNIYA HANN
//                    SIRF UI TEYAAR HOII HAI
                    Spacer(modifier = Modifier.width(5.dp).fillMaxHeight())

                    TextField(
                        modifier = Modifier.weight(1f),
                        value = transactionState.fuelPrice.toValidDecimalString(),
                        onValueChange = { newText ->
                            val filteredValue = filterNumericDecimalInput(newText)
                            if (filteredValue.length <= 6) { // limit input length
                                // Convert safely to Double
                                val doubleValue = filteredValue.toDoubleOrNull() ?: 0.0
                                viewModel.onFuelPriceChanged(doubleValue)
                            }
                        }, // Update the state with new text
                        label = { Text("Price/L") }, // Optional label
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
//                    isError = amountError != null && !amountError.equals("untouched")
                    )
                }
            }

            TextField(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                value = transactionState.totalKms.toValidLongString(),
                onValueChange = { newText ->
                    val filteredValue = filterNumericDecimalInput(newText)
                    if (newText.length <= 9) {
                        val doubleValue = filteredValue.toDoubleOrNull() ?: 0.0
                        viewModel.onTotalKmChanged(doubleValue)
                    }
                },
                label = { Text("Odometer(KMs)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = totalKmsError != null && !totalKmsError.equals("untouched")
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = transactionState.amount.toValidDecimalString(),
                onValueChange = { newText ->
                    val filteredValue = filterNumericDecimalInput(newText)
                    if (filteredValue.length <= 6) { // limit input length
                        // Convert safely to Double
                        val doubleValue = filteredValue.toDoubleOrNull() ?: 0.0
                        viewModel.onAmountChanged(doubleValue)
                    }
                }, // Update the state with new text
                label = { Text("Amount") }, // Optional label
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = amountError != null && !amountError.equals("untouched")
            )

            TextField(
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(),
                maxLines = 4,
                singleLine = false,
                value = transactionState.notes,
                onValueChange = { newText ->
                    viewModel.onNotesChanged(newText)
                }, // Update the state with new text
                label = { Text("Note") }, // Optional label
            )


            Button(
                enabled = isValid,
                onClick = {
                    // IF ITS NOT A GAS EXPENSE ENTRY
                    if (selectedEntryType.equals(entryTypeOptions[0]) ||
                        (selectedEntryType.equals(entryTypeOptions[1]) && !selectedExpenseType.equals(expenseTypeOptions[0]))
                    ) {
                        viewModel.onFuelPriceChanged(0.00)
                    }

                    viewModel.submit()
                    onDismissed()
                },
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text("Save", fontSize = 16.sp)
            }
        }
    }
}

fun filterNumericDecimalInput(input: String): String {
    val decimalSeparator = '.' // Or locale-specific separator if needed
    var result = ""
    var hasDecimal = false

    for (char in input) {
        if (char.isDigit()) {
            result += char
        } else if (char == decimalSeparator && !hasDecimal) {
            result += char
            hasDecimal = true
        }
    }
    return result
}