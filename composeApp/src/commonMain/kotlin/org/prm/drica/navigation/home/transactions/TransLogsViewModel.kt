package org.prm.drica.navigation.home.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.prm.drica.db.DriCaDatabase
import org.prm.drica.models.TransactionDataModel

/*
* Created by parambirsingh ON 31/10/25
*/
class TransLogsViewModel(database: DriCaDatabase) : ViewModel() {
    val transactionDao = database.getTransactionDao()

    fun deleteEntry(itemToDelete: TransactionDataModel) {
        viewModelScope.launch {
            transactionDao.deleteOne(itemToDelete)
        }
    }

    val transactions: StateFlow<List<TransactionDataModel>> =
        transactionDao.getAll()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    private val _transactionState = MutableStateFlow(TransactionDataModel())
    val transactionState: StateFlow<TransactionDataModel> = _transactionState
}