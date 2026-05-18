package org.prm.drica.navigation.home.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.prm.drica.db.DriCaDatabase
import org.prm.drica.models.TransactionDataModel
import org.prm.drica.utils.calculateMileagePerLitreFromGasFills
import org.prm.drica.utils.getCurrentMonthRange
import org.prm.drica.utils.getLastMonthRange
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

    private val _totalEarningsThisMonth = MutableStateFlow(0.0)
    val totalEarningsThisMonth: StateFlow<Double> = _totalEarningsThisMonth

    private val _totalProfit = MutableStateFlow(0.0)
    val totalProfit: StateFlow<Double> = _totalProfit

    private val _totalProfitThisMonth = MutableStateFlow(0.0)
    val totalProfitThisMonth: StateFlow<Double> = _totalProfitThisMonth

    private val _totalEarningsLastMonth = MutableStateFlow(0.0)
    val totalEarningsLastMonth: StateFlow<Double> = _totalEarningsLastMonth

    private val _totalProfitLastMonth = MutableStateFlow(0.0)
    val totalProfitLastMonth: StateFlow<Double> = _totalProfitLastMonth

    private val _kmDriveThisMonth = MutableStateFlow(0L)
    val kmDriveThisMonth: StateFlow<Long> = _kmDriveThisMonth

    private val _kmDriveLastMonth = MutableStateFlow(0L)
    val kmDriveLastMonth: StateFlow<Long> = _kmDriveLastMonth

    private val _mileagePerLitreThisMonth = MutableStateFlow(0.0)
    val mileagePerLitreThisMonth: StateFlow<Double> = _mileagePerLitreThisMonth

    private val _mileagePerLitreLastMonth = MutableStateFlow(0.0)
    val mileagePerLitreLastMonth: StateFlow<Double> = _mileagePerLitreLastMonth

    private val _daysWorked = MutableStateFlow(0)
    val daysWorked: StateFlow<Int> = _daysWorked

    fun loadData() {
        viewModelScope.launch {
            val sum = transactionDao.getTotalEarnings() ?: 0.0
            _totalEarnings.value = sum

            val profit = transactionDao.getTotalProfit() ?: 0.0
            _totalProfit.value = profit

            val (start, end) = getCurrentMonthRange()
            val profitThisMonth = transactionDao.getTotalProfitThisMonth(start, end) ?: 0.0
            _totalProfitThisMonth.value = profitThisMonth

            val sumThisMonth = transactionDao.getTotalEarningsThisMonth(start, end) ?: 0.0
            _totalEarningsThisMonth.value = sumThisMonth

            val (startOfLastMonth, endOfLastMonth) = getLastMonthRange()
            val profitLastMonth = transactionDao.getTotalProfitThisMonth(startOfLastMonth, endOfLastMonth) ?: 0.0
            _totalProfitLastMonth.value = profitLastMonth

            val sumLastMonth = transactionDao.getTotalEarningsThisMonth(startOfLastMonth, endOfLastMonth) ?: 0.0
            _totalEarningsLastMonth.value = sumLastMonth

            val kmDriveThisMonth = transactionDao.getKmRange(start, end)
            if (kmDriveThisMonth?.lastKm != null && kmDriveThisMonth?.firstKm != null) {
                _kmDriveThisMonth.value = kmDriveThisMonth.lastKm - kmDriveThisMonth.firstKm
            }

            val kmDriveLastMonth = transactionDao.getKmRange(startOfLastMonth, endOfLastMonth)
            if (kmDriveLastMonth?.lastKm != null && kmDriveLastMonth?.firstKm != null) {
                _kmDriveLastMonth.value = kmDriveLastMonth.lastKm - kmDriveLastMonth.firstKm
            }

            val gasFills = transactionDao.getGasTransactionsOrdered()
            _mileagePerLitreThisMonth.value =
                calculateMileagePerLitreFromGasFills(gasFills, start, end)
            _mileagePerLitreLastMonth.value =
                calculateMileagePerLitreFromGasFills(gasFills, startOfLastMonth, endOfLastMonth)
        }
        loadNumberOfDaysWorked()
    }

    @OptIn(ExperimentalTime::class)
    fun loadNumberOfDaysWorked() {
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

    fun calculateEarningsPerKm(totalEarnings: Double, totalKm: Double): Double {
        return if (totalKm != 0.0) {
            totalEarnings / totalKm
        } else {
            0.0 // avoid division by zero
        }
    }

    suspend fun getLineChartData(): List<List<ChartPoint>> {
        val (start, end) = getCurrentMonthRange()
        val transactions = transactionDao.getTransactionsForDateRange(start, end).first()
        val listOfIncome = transactions
//            .filter { it.totalKms > 0 }
            .sortedBy { it.dateTime }
            .mapIndexedNotNull { index, transaction ->
                val income = if (transaction.amount > 0.0) transaction.amount else null
                if (income == null || income.isNaN() || income.isInfinite()) {
                    null
                } else {
                    ChartPoint(
                        x = index.toInt(),
                        y = income.toFloat()
                    )
                }
            }
        val listOfExpense = transactions
//            .filter { it.totalKms > 0 }
            .sortedBy { it.dateTime }
            .mapIndexedNotNull { index, transaction ->
                val income = if (transaction.amount < 0.0) -transaction.amount else null
                if (income == null || income.isNaN() || income.isInfinite()) {
                    null
                } else {
                    ChartPoint(
                        x = index.toInt(),
                        y = income.toFloat()
                    )
                }
            }
        return listOf(listOfIncome, listOfExpense)
    }
}

data class ChartPoint(
    val x: Int,
    val y: Float
)