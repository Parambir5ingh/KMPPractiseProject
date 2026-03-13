package org.prm.drica

import androidx.lifecycle.ViewModel
import com.mohamedrejeb.calf.io.KmpFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.prm.drica.db.DriCaDatabase
import org.prm.drica.models.TransactionDataModel
import org.prm.drica.navigation.settings.readJsonFromFile

/*
* Created by parambirsingh ON 31/10/25
*/
class AppViewModel(database: DriCaDatabase) : ViewModel() {
    val transactionDao = database.getTransactionDao()

    private val _transactionsData = MutableStateFlow<String?>(null)
    val transactionsData: StateFlow<String?> = _transactionsData

    private val _importedTransactionsData = MutableStateFlow<String?>(null)
    val importedTransactionsData: StateFlow<String?> = _importedTransactionsData

    suspend fun onExportClicked() {
        _transactionsData.value = exportAppDataCommon()
    }

    fun onExportDone() {
        _transactionsData.value = null
    }

    private suspend fun exportAppDataCommon(): String {
        return Json.encodeToString(transactionDao.getAll().first())
    }

    suspend fun onFileImported(file: KmpFile?) {
        file?.let {
            val jsonString = readJsonFromFile(file)

            // parse json
//            val json = Json.parseToJsonElement(jsonString)

            transactionDao.upsert(parseTransactions(jsonString))

            println("IMPORTED DATA : " + jsonString)
        }
    }

    suspend fun parseTransactions(jsonString: String): List<TransactionDataModel> {
        return try {
            Json { ignoreUnknownKeys = true } // ignore extra fields
                .decodeFromString(jsonString)
        } catch (e: Exception) {
            emptyList() // or handle error
        }
    }


}