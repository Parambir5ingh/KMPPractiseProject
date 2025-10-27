package org.prm.drica.db

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
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
        .build()
}