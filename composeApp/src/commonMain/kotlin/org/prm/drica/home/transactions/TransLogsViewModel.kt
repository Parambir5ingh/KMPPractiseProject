package org.prm.drica.home.transactions

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.prm.drica.db.DriCaDatabase
import org.prm.drica.models.TransactionDataModel

/*
* Created by parambirsingh ON 31/10/25
*/
class TransLogsViewModel(database : DriCaDatabase) : ViewModel() {
    private val _transactionState = MutableStateFlow(TransactionDataModel())
    val transactionState: StateFlow<TransactionDataModel> = _transactionState

    val transactionDao = database.getTransactionDao()
}