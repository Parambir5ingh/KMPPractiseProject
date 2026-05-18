package org.prm.drica.navigation.home.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.prm.drica.db.DriCaDatabase
import org.prm.drica.models.TransactionDataModel
import org.prm.drica.utils.getCurrentMonthRange

/**
 * Dashboard state is a single list of monthly summaries instead of separate
 * StateFlows per period (this month / last month / overall).
 */
class DashboardViewModel(database: DriCaDatabase) : ViewModel() {
    val transactionDao = database.getTransactionDao()

    private val _monthSummaries = MutableStateFlow<List<MonthDashboardSummary>>(emptyList())
    val monthSummaries: StateFlow<List<MonthDashboardSummary>> = _monthSummaries.asStateFlow()

    /**
     * Recomputes all month cards when transactions change.
     * Called from the UI with the latest list from [transactionDao.getAll].
     */
    fun loadData(transactions: List<TransactionDataModel>) {
        viewModelScope.launch {
            val gasFills = transactionDao.getGasTransactionsOrdered()
            _monthSummaries.value = buildMonthSummaries(transactions, gasFills)
        }
    }

    /**
     * Two series for the line chart: current-month income and expenses (positive values).
     * Chart is optional in the UI; data is still prepared here for when it is enabled.
     */
    suspend fun getLineChartData(): List<List<ChartPoint>> {
        val (start, end) = getCurrentMonthRange()
        val transactions = transactionDao.getTransactionsForDateRange(start, end).first()
        val listOfIncome = transactions
            .sortedBy { it.dateTime }
            .mapIndexedNotNull { index, transaction ->
                val income = if (transaction.amount > 0.0) transaction.amount else null
                if (income == null || income.isNaN() || income.isInfinite()) null
                else ChartPoint(x = index, y = income.toFloat())
            }
        val listOfExpense = transactions
            .sortedBy { it.dateTime }
            .mapIndexedNotNull { index, transaction ->
                // Expenses are stored negative in DB; chart uses positive magnitude on the Y axis.
                val expense = if (transaction.amount < 0.0) -transaction.amount else null
                if (expense == null || expense.isNaN() || expense.isInfinite()) null
                else ChartPoint(x = index, y = expense.toFloat())
            }
        return listOf(listOfIncome, listOfExpense)
    }
}

data class ChartPoint(
    val x: Int,
    val y: Float,
)
