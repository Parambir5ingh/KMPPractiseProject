package org.prm.drica.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import org.prm.drica.models.TransactionDataModel

@Dao
interface TransactionsDao {
    @Upsert
    suspend fun upsert(item: TransactionDataModel)

    @Query("SELECT count(*) FROM TransactionData")
    suspend fun count(): Int

    @Query("SELECT * FROM TransactionData ORDER BY dateTime DESC LIMIT 1")
    suspend fun getLastTransaction(): TransactionDataModel?

    @Query("SELECT * FROM TransactionData ORDER BY dateTime DESC")
    fun getAll(): Flow<List<TransactionDataModel>>

    @Query("SELECT SUM(amount) FROM TransactionData")
    suspend fun getTotalProfit(): Double?

    @Query("SELECT SUM(amount) FROM TransactionData WHERE amount > 0")
    suspend fun getTotalEarnings(): Double?

    @Query("SELECT dateTime FROM TransactionData")
    suspend fun getAllTransactionTimestamps(): List<Long>
}