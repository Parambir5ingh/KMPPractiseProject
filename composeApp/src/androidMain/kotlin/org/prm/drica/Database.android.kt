package org.prm.drica

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.prm.drica.db.DriCaDatabase

/*
* Created by parambirsingh ON 25/10/25
*/
fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<DriCaDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("drica.db")
    return Room.databaseBuilder<DriCaDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}