package org.prm.drica.addnew

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.prm.drica.ui.Dropdown
import org.prm.drica.utils.toValidString

@Composable
@Preview
fun PreviewAddNew() {
//    AddNew({}, AddNewViewModel())
}

@Composable
fun AddNew(onDismissed: () -> Unit, viewModel: AddNewViewModel) {
//    var transactionData by remember { mutableStateOf<TransactionDataModel?>(null) }
    val transactionState by viewModel.transactionState.collectAsState()
    val amountError by viewModel.amountError.collectAsState()
    val totalKmsError by viewModel.totalKmsError.collectAsState()
    val isValid by viewModel.isValid.collectAsState()

    val entryTypeOptions by viewModel.entryTypeOptions.collectAsState()
    val expenseTypeOptions by viewModel.expenseTypeOptions.collectAsState()
    val incomeTypeOptions by viewModel.incomeTypeOptions.collectAsState()
    val selectedEntryType by viewModel.selectedEntryType.collectAsState()
    val selectedExpenseType by viewModel.selectedExpenseType.collectAsState()
    val selectedIncomeType by viewModel.selectedIncomeType.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onSelectedEntryType(entryTypeOptions.first())
        viewModel.onSelectedCategoryType(incomeTypeOptions.first())
    }

    Column(
        modifier = Modifier.fillMaxWidth().background(Color.White),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Column(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Dropdown("Entry Type", entryTypeOptions, selectedValue = selectedEntryType, onSelectOption = {
                viewModel.onSelectedEntryType(it)
            })

            if (selectedEntryType == entryTypeOptions[0]) { // Income
                Dropdown(
                    "Income Type",
                    incomeTypeOptions,
                    selectedValue = selectedIncomeType,
                    onSelectOption = { viewModel.onSelectedCategoryType(it) } // Correct
                )
            } else { // Expense
                Dropdown(
                    "Expense Type",
                    expenseTypeOptions,
                    selectedValue = selectedExpenseType,
                    onSelectOption = { viewModel.onSelectedCategoryType(it) }
                )
            }


            TextField(
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(),
                value = transactionState.amount.toValidString(),
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

            Row {
                TextField(
                    modifier = Modifier.weight(1f),
                    value = transactionState.tripKms.toString(),
                    onValueChange = { newText ->
                        val filteredValue = filterNumericDecimalInput(newText)
                        if (newText.length <= 10) {
                            val doubleValue = filteredValue.toDoubleOrNull() ?: 0.0
                            viewModel.onTripKmChanged(doubleValue)
                        }
                    },
                    label = { Text("KMs(Trip)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Spacer(modifier = Modifier.width(10.dp))

                TextField(
                    modifier = Modifier.weight(1f),
                    value = transactionState.totalKms.toValidString(),
                    onValueChange = { newText ->
                        val filteredValue = filterNumericDecimalInput(newText)
                        if (newText.length <= 10) {
                            val doubleValue = filteredValue.toDoubleOrNull() ?: 0.0
                            viewModel.onTotalKmChanged(doubleValue)
                        }
                    },
                    label = { Text("Total KMs") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = totalKmsError != null && !totalKmsError.equals("untouched")
                )
            }

            TextField(
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(),
                maxLines = 4,
                singleLine = false,
                value = transactionState.notes,
                onValueChange = { newText ->
                    viewModel.onNotesChanged(newText)
                }, // Update the state with new text
                label = { Text("Notes") }, // Optional label
            )


            Button(
                enabled = isValid,
                onClick = {
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