package org.prm.drica

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.prm.drica.db.DriCaDatabase

/*
* Created by parambirsingh ON 31/10/25
*/
class AppViewModel(database: DriCaDatabase) : ViewModel() {
    val transactionDao = database.getTransactionDao()

    private val _transactionsData = MutableStateFlow<String?>(null)
    val transactionsData: StateFlow<String?> = _transactionsData

    suspend fun onExportClicked() {
        _transactionsData.value = exportAppDataCommon()
    }

    fun onExportDone() {
        _transactionsData.value = null
    }

    private suspend fun exportAppDataCommon(): String {
        return Json.encodeToString(transactionDao.getAll().first())
//        ExportAppData.exportAppData(transactionsJson)

//        viewModelScope.launch {
//            if (transactionState.value.type.equals(entryTypeOptions.value[1])) { // IF ITS EXPENSE, IT WILL BE SAVED AS NEGATIVE VALUE
//                onAmountChanged(-transactionState.value.amount)
//            }
//            transactionDao.upsert(transactionState.value)
//            _transactionState.update { TransactionDataModel() }
//        }
    }

}