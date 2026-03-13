package org.prm.drica.navigation.settings

import com.mohamedrejeb.calf.io.KmpFile
import org.prm.drica.AppContextProvider

actual suspend fun readJsonFromFile(file: KmpFile): String {
    val context = AppContextProvider.applicationContext
    val uri = file.uri

    return context.contentResolver.openInputStream(uri)?.bufferedReader().use {
        it?.readText() ?: ""
    }
}