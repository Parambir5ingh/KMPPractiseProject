package org.prm.drica.navigation.addnew

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.prm.drica.db.DriCaDatabase
import org.prm.drica.models.TransactionDataModel
import org.prm.drica.models.VehiclesModel
import org.prm.drica.utils.Validator
import kotlin.time.ExperimentalTime

/*
* Created by parambirsingh ON 31/10/25
*/
class AddTransactionViewModel(database: DriCaDatabase) : ViewModel() {
    val transactionDao = database.getTransactionDao()
    val vehiclesDao = database.getVehiclesDao()

    private val _transactionState = MutableStateFlow(TransactionDataModel())
    val transactionState: StateFlow<TransactionDataModel> = _transactionState

    private val _amountError = MutableStateFlow<String?>("untouched")
    var amountError: StateFlow<String?> = _amountError

    private val _totalKmsError = MutableStateFlow<String?>(null)
    var totalKmsError: StateFlow<String?> = _totalKmsError

    private val _isValid = MutableStateFlow(false)
    var isValid: StateFlow<Boolean> = _isValid

    private val _entryTypeOptions = MutableStateFlow(arrayOf("Income", "Expense"))
    val entryTypeOptions: StateFlow<Array<String>> = _entryTypeOptions

    private val _vehicleList = MutableStateFlow<List<VehiclesModel>>(emptyList())
    val vehicleList: StateFlow<List<VehiclesModel>> = _vehicleList.asStateFlow()

    private val _expenseTypeOptions = MutableStateFlow(arrayOf("Gas", "General Repair", "Major Repair", "Tyres", "Accessories"))
    val expenseTypeOptions: StateFlow<Array<String>> = _expenseTypeOptions

    private val _incomeTypeOptions = MutableStateFlow(arrayOf("Deliveries", "Cab", "Insurance Claim", "Tax Return"))
    val incomeTypeOptions: StateFlow<Array<String>> = _incomeTypeOptions

    private val _selectedEntryType = MutableStateFlow(_entryTypeOptions.value.first())
    val selectedEntryType: StateFlow<String> = _selectedEntryType

    private val _selectedVehicle = MutableStateFlow<VehiclesModel?>(null)
    val selectedVehicle: StateFlow<VehiclesModel?> = _selectedVehicle

    private val _selectedExpenseType = MutableStateFlow(_expenseTypeOptions.value.first())
    val selectedExpenseType: StateFlow<String> = _selectedExpenseType

    private val _selectedIncomeType = MutableStateFlow(_incomeTypeOptions.value.first())
    val selectedIncomeType: StateFlow<String> = _selectedIncomeType

    init {
        viewModelScope.launch {
            vehiclesDao.getAll().collect { list ->
                _vehicleList.value = list.map { vehicle ->
                    VehiclesModel(
                        id = vehicle.id,
                        name = vehicle.name,
                        type = vehicle.type,
                        year = vehicle.year,
                        kms = vehicle.kms
                    )
                }
                if (vehicleList.value.isNotEmpty()) {
                    onSelectedVehicle(vehicleList.value[0])
                }
            }
        }
    }

    fun onFuelPriceChanged(value: Double) {
        _transactionState.update { it.copy(fuelPrice = value) }
        validateForm()
    }

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

    fun onSelectedVehicle(model: VehiclesModel) {
        _transactionState.update { it.copy(vehicleId = model.id) }
        _selectedVehicle.value = model
    }

    fun onSelectedEntryType(type: String) {
        val category = if (type == "Income") {
            _incomeTypeOptions.value.first().also { _selectedIncomeType.value = it }
        } else {
            _expenseTypeOptions.value.first().also { _selectedExpenseType.value = it }
        }

        _selectedEntryType.value = type
        _transactionState.update { it.copy(type = type, category = category) }
        if (type == "Expense" && category == "Gas") {
            loadLastFuelPrice()
        }
    }

    fun onSelectedCategoryType(category: String) {
        _transactionState.update { it.copy(category = category) }
        if (category in _expenseTypeOptions.value) {
            _selectedExpenseType.value = category
        } else {
            _selectedIncomeType.value = category
        }
        if (_selectedEntryType.value == "Expense" && category == "Gas") {
            loadLastFuelPrice()
        }
    }

    /** Called when the add-transaction sheet opens. Resets form and prefills defaults. */
    fun prepareNewTransaction() {
        _amountError.value = "untouched"
        _totalKmsError.value = null
        _isValid.value = false
        _selectedEntryType.value = _entryTypeOptions.value.first()
        _selectedExpenseType.value = _expenseTypeOptions.value.first()
        _selectedIncomeType.value = _incomeTypeOptions.value.first()
        _transactionState.value = TransactionDataModel(
            type = _entryTypeOptions.value.first(),
            category = _incomeTypeOptions.value.first(),
        )
        viewModelScope.launch {
            transactionDao.getLastTransaction()?.totalKms?.let { onTotalKmChanged(it) }
        }
    }

    private fun loadLastFuelPrice() {
        viewModelScope.launch {
            transactionDao.getLastFuelPrice()?.let { last ->
                _transactionState.update { it.copy(fuelPrice = last.fuelPrice) }
            }
        }
    }

    fun onNotesChanged(notes: String) {
        _transactionState.update { it.copy(notes = notes) }
    }

    private fun validateForm() {
        _isValid.value = _amountError.value == null && _totalKmsError.value == null
    }

    fun submit() {
        viewModelScope.launch {
            // SAVING TRANSACTION BY UPDATING CORRECT AMOUNT(PLACING MINUS SIGN IF ITS AN EXPENSE)
            if (transactionState.value.type.equals(entryTypeOptions.value[1])) { // IF ITS EXPENSE, IT WILL BE SAVED AS NEGATIVE VALUE
                onAmountChanged(-transactionState.value.amount)
            }
            transactionDao.upsert(transactionState.value)

            // UPDATING VEHICLE'S ODOMETER ON SUBMITTING A TRANSACTION
            if (transactionState.value.vehicleId != 0L) {
                val vehicle = vehiclesDao.getVehicleById(transactionState.value.vehicleId.toString())
                vehicle?.let { vehicle ->
                    val updatedVehicle = vehicle.copy(
                        kms = transactionState.value.totalKms.toString()
                    )
                    vehiclesDao.upsert(updatedVehicle)
                }
            }

            prepareNewTransaction()
        }
    }

    @OptIn(ExperimentalTime::class)
    fun onSelectedDate(date: String, dateTimeMilis: Long) {
        println("Selected Date: $date, Miliseconds : $dateTimeMilis")
        _transactionState.update { it.copy(dateTime = dateTimeMilis) }
    }
}