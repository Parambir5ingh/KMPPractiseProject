package org.prm.drica.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import org.prm.drica.TransactionDataModel

@Database(
    entities = [TransactionDataModel::class],
    version = 1
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class DriCaDatabase : RoomDatabase() {
    abstract fun getTransactionDao(): TransactionsDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<DriCaDatabase> {
    override fun initialize(): DriCaDatabase
}
