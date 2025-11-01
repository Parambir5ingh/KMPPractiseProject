package org.prm.drica.addnew

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.prm.drica.db.DriCaDatabase
import org.prm.drica.models.TransactionDataModel
import org.prm.drica.utils.Validator
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/*
* Created by parambirsingh ON 31/10/25
*/
class AddNewViewModel(database: DriCaDatabase) : ViewModel() {
    private val transactionDao = database.getTransactionDao()

    private val _transactionState = MutableStateFlow(TransactionDataModel())
    val transactionState: StateFlow<TransactionDataModel> = _transactionState

    private val _amountError = MutableStateFlow<String?>(null)
    var amountError: StateFlow<String?> = _amountError

    private val _totalKmsError = MutableStateFlow<String?>(null)
    var totalKmsError: StateFlow<String?> = _totalKmsError

    private val _isValid = MutableStateFlow(false)
    var isValid: StateFlow<Boolean> = _isValid

    private val _entryTypeOptions = MutableStateFlow(arrayOf("Income", "Expense"))
    val entryTypeOptions: StateFlow<Array<String>> = _entryTypeOptions

    private val _expenseTypeOptions = MutableStateFlow(arrayOf("Gas", "General Repair", "Special Repair", "Tyres", "Accessories"))
    val expenseTypeOptions: StateFlow<Array<String>> = _expenseTypeOptions

    private val _incomeTypeOptions = MutableStateFlow(arrayOf("Food Deliveries", "Cab", "Insurance Claim", "Tax Return"))
    val incomeTypeOptions: StateFlow<Array<String>> = _incomeTypeOptions

    private val _selectedEntryType = MutableStateFlow(_entryTypeOptions.value.first())
    val selectedEntryType: StateFlow<String> = _selectedEntryType

    private val _selectedExpenseType = MutableStateFlow(_expenseTypeOptions.value.first())
    val selectedExpenseType: StateFlow<String> = _selectedExpenseType

    private val _selectedIncomeType = MutableStateFlow(_incomeTypeOptions.value.first())
    val selectedIncomeType: StateFlow<String> = _selectedIncomeType


    fun onAmountChanged(value: Double) {
        _transactionState.update { it.copy(amount = value) }
        _amountError.value = Validator.validateDouble(value, "Amount")
        validateForm()
    }

    fun onTotalKmChanged(value: Double) {
        _transactionState.update { it.copy(totalKms = value) }
        _totalKmsError.value = Validator.validateDouble(value, "Kilometers")
        validateForm()
    }

    fun onSelectedEntryType(type: String) {
        _transactionState.update { it.copy(type = type) }

        if (type == "Income") {
            _selectedIncomeType.value = _incomeTypeOptions.value.first()
        } else {
            _selectedExpenseType.value = _expenseTypeOptions.value.first()
        }

        _selectedEntryType.value = type
    }

    fun onSelectedCategoryType(category: String) {
        _transactionState.update { it.copy(category = category) }
        if (category in _expenseTypeOptions.value)
            _selectedExpenseType.value = category
        else
            _selectedIncomeType.value = category
    }

    fun onNotesChanged(notes: String) {
        _transactionState.update { it.copy(notes = notes) }
    }

    private fun validateForm() {
        _isValid.value = listOf(
            _amountError.value,
            _totalKmsError.value
        ).all { it == null }
    }

    @OptIn(ExperimentalTime::class)
    fun submit() {
        _transactionState.update { it.copy(dateTime = Clock.System.now().toEpochMilliseconds()) }
        viewModelScope.launch {
            transactionDao.upsert(transactionState.value)
            _transactionState.update { TransactionDataModel() }
        }
    }
}