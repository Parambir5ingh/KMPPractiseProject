package org.prm.drica.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import org.prm.drica.models.TransactionDataModel
import org.prm.drica.models.VehiclesModel

@Database(
    entities = [TransactionDataModel::class, VehiclesModel::class],
    version = 2
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class DriCaDatabase : RoomDatabase() {
    abstract fun getTransactionDao(): TransactionsDao
    abstract fun getVehiclesDao(): VehiclesDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<DriCaDatabase> {
    override fun initialize(): DriCaDatabase
}
