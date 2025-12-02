package org.prm.drica.home.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.prm.drica.db.DriCaDatabase
import org.prm.drica.models.TransactionDataModel
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/*
* Created by parambirsingh ON 31/10/25
*/
class DashboardViewModel(database: DriCaDatabase) : ViewModel() {
    private val _transactionState = MutableStateFlow(TransactionDataModel())
    val transactionState: StateFlow<TransactionDataModel> = _transactionState

    val transactionDao = database.getTransactionDao()

    private val _totalEarnings = MutableStateFlow(0.0)
    val totalEarnings: StateFlow<Double> = _totalEarnings

    private val _totalProfit = MutableStateFlow(0.0)
    val totalProfit: StateFlow<Double> = _totalProfit

    private val _daysWorked = MutableStateFlow(0)
    val daysWorked: StateFlow<Int> = _daysWorked

    fun loadData() {
        viewModelScope.launch {
            val sum = transactionDao.getTotalEarnings() ?: 0.0
            _totalEarnings.value = sum

            val profit = transactionDao.getTotalProfit() ?: 0.0
            _totalProfit.value = profit
        }
        loadNumberOfDaysWorked()
    }

    @OptIn(ExperimentalTime::class)
    fun loadNumberOfDaysWorked(){
        viewModelScope.launch {
            val timestamps = transactionDao.getAllTransactionTimestamps()

            val uniqueDays = timestamps
                .map { ts ->
                    Instant.fromEpochMilliseconds(ts)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date
                }
                .toSet()
                .size

            _daysWorked.value = uniqueDays
        }
    }
}