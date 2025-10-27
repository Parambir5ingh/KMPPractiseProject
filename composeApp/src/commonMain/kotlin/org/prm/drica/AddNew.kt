package org.prm.drica

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun PreviewAddNew() {
    AddNew({})
}

@Composable
fun AddNew(onDismissed: () -> Unit) {

    var amount by remember { mutableStateOf("") }
    var kilometers by remember { mutableStateOf("") }
    var entryTypeOptions by remember { mutableStateOf(arrayOf("Income", "Expense")) }
    var expenseTypeOptions by remember { mutableStateOf(arrayOf("Gas", "General Repair", "Special Repair", "Tyres", "Wipers", "Accessories")) }
    var selectedExpenseType by remember { mutableStateOf(expenseTypeOptions[0]) }
    var selectedEntryType by remember { mutableStateOf(entryTypeOptions[0]) }

    Column(
        modifier = Modifier.fillMaxWidth().background(Color.White),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Column(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(),
                value = amount,
                onValueChange = { newText ->
                    val filteredValue = filterNumericDecimalInput(newText)
                    if (newText.length <= 6) { // Example limit of 10 characters
                        amount = filteredValue
                    }
                }, // Update the state with new text
                label = { Text("Amount") }, // Optional label
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Dropdown("Entry Type", entryTypeOptions, selectedValue = selectedEntryType, onSelectOption = {
                selectedEntryType = it
            })

            Dropdown("Expense Type", expenseTypeOptions, selectedValue = selectedExpenseType, onSelectOption = {
                selectedExpenseType = it
            })

            TextField(
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(),
                value = kilometers,
                onValueChange = { newText ->
                    val filteredValue = filterNumericDecimalInput(newText)
                    if (newText.length <= 10) {
                        kilometers = filteredValue
                    }
                },
                label = { Text("Kilometers") }, // Optional label
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Button(
                onClick = {
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