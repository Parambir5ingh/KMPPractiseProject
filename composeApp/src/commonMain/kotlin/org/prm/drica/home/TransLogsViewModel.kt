package org.prm.drica.home

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.prm.drica.db.DriCaDatabase
import org.prm.drica.db.TransactionsDao
import org.prm.drica.models.TransactionDataModel
import org.prm.drica.utils.Validator

/*
* Created by parambirsingh ON 31/10/25
*/
class TransLogsViewModel(database : DriCaDatabase) {
    private val _transactionState = MutableStateFlow(TransactionDataModel())
    val transactionState: StateFlow<TransactionDataModel> = _transactionState

    val transactionDao = database.getTransactionDao()
}