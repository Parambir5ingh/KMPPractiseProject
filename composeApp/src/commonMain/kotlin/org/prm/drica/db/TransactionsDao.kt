package org.prm.drica.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import org.prm.drica.TransactionDataModel

@Dao
interface TransactionsDao {
    @Upsert
    suspend fun upsert(item: TransactionDataModel)

    @Query("SELECT count(*) FROM TransactionData")
    suspend fun count(): Int

    @Query("SELECT * FROM TransactionData")
    fun getAll(): Flow<List<TransactionDataModel>>
}