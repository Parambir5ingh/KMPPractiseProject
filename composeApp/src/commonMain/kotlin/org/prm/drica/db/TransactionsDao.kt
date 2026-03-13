package org.prm.drica.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import org.prm.drica.models.KMRangeModel
import org.prm.drica.models.TransactionDataModel

@Dao
interface TransactionsDao {
    @Upsert
    suspend fun upsert(item: TransactionDataModel)

    @Upsert
    suspend fun upsert(item: List<TransactionDataModel>)

    @Query("SELECT count(*) FROM TransactionData")
    suspend fun count(): Int

    @Query("SELECT * FROM TransactionData ORDER BY dateTime DESC, totalKms DESC LIMIT 1")
    suspend fun getLastTransaction(): TransactionDataModel?

    @Query("SELECT * FROM TransactionData ORDER BY dateTime DESC, totalKms DESC")
    fun getAll(): Flow<List<TransactionDataModel>>

    @Query("SELECT SUM(amount) FROM TransactionData")
    suspend fun getTotalProfit(): Double?

    @Query("SELECT SUM(amount) FROM TransactionData WHERE dateTime >= :start AND dateTime < :end")
    suspend fun getTotalProfitThisMonth(start : Long, end : Long): Double?

    @Query("SELECT SUM(amount) FROM TransactionData WHERE amount > 0")
    suspend fun getTotalEarnings(): Double?

    @Query("SELECT SUM(amount) FROM TransactionData WHERE amount > 0 AND dateTime >= :start AND dateTime < :end")
    suspend fun getTotalEarningsThisMonth(start : Long, end : Long): Double?

    @Query("SELECT (SELECT totalKms FROM TransactionData WHERE dateTime >= :start AND dateTime < :end ORDER BY dateTime ASC LIMIT 1) AS firstKm," +
            "(SELECT totalKms FROM TransactionData WHERE dateTime >= :start AND dateTime < :end ORDER BY dateTime DESC LIMIT 1) AS lastKm;")
    suspend fun getKmRange(start : Long, end : Long): KMRangeModel?

    @Query("SELECT dateTime FROM TransactionData")
    suspend fun getAllTransactionTimestamps(): List<Long>

    @Delete
    suspend fun deleteOne(transactionDataModel: TransactionDataModel)
}