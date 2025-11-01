package org.prm.drica

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import org.prm.drica.db.getRoomDatabase

fun MainViewController() = ComposeUIViewController {

    val database = remember {
        getRoomDatabase(getDatabaseBuilder())
    }

    App(database)
}