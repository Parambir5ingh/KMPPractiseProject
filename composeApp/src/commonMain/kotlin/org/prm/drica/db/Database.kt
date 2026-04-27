package org.prm.drica.db

import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import kotlinx.coroutines.Dispatchers


/*
* Created by parambirsingh ON 25/10/25
*/
fun getRoomDatabase(
    builder: RoomDatabase.Builder<DriCaDatabase>
): DriCaDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .addMigrations(MIGRATION_1_2)
        .build()
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "ALTER TABLE TransactionData ADD COLUMN fuelPrice REAL NOT NULL DEFAULT 0.0"
        )
    }
}