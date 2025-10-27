package org.prm.drica

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import org.prm.drica.db.DriCaDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/*
* Created by parambirsingh ON 25/10/25
*/
fun getDatabaseBuilder(): RoomDatabase.Builder<DriCaDatabase> {
    val dbFilePath = documentDirectory() + "/drica.db"
    return Room.databaseBuilder<DriCaDatabase>(
        name = dbFilePath,
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}