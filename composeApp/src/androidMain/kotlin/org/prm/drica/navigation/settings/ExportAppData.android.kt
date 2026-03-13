package org.prm.drica.navigation.settings

import android.content.Intent
import androidx.core.content.FileProvider
import org.prm.drica.AppContextProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual object ExportAppData {
    actual fun exportAppData(data: String) {
        val context = AppContextProvider.applicationContext

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Date())

        val fileName = "dricaAppData_$today.txt"

        val file = File(context.cacheDir, fileName)
        file.writeText(data)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Export via")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}