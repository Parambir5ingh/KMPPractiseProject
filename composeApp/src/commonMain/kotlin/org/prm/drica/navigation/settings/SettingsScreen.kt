package org.prm.drica.navigation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.calf.core.LocalPlatformContext
import com.mohamedrejeb.calf.io.KmpFile
import com.mohamedrejeb.calf.io.getPath
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import kotlinx.coroutines.launch
import org.prm.drica.db.DriCaDatabase
import org.prm.drica.ui.TitleBar

@Composable
fun SettingsScreen(
    database: DriCaDatabase,
    onBackPressed: () -> Unit,
    onExportDataClicked: () -> Unit,
    onImportedFile: (filePath: KmpFile?) -> Unit,
    onVehicleManagementClicked: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalPlatformContext.current

    var byteArrayOfImportedFile by remember { mutableStateOf(ByteArray(0)) }
    var importedFilePath by remember { mutableStateOf("") }
    var platformSpecificImportedFile by remember { mutableStateOf<KmpFile?>(null) }

    val pickerLauncher = rememberFilePickerLauncher(
        type = FilePickerFileType.Text,
        selectionMode = FilePickerSelectionMode.Single,
        onResult = { file ->
            scope.launch {
                file.firstOrNull()?.let {
//                    byteArrayOfImportedFile = it.readByteArray(context)
//                    importedFilePath = it.getPath(context) ?: ""
                    platformSpecificImportedFile = it
                    onImportedFile(platformSpecificImportedFile)
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(Color.White)
    ) {

        TitleBar(
            title = "Settings",
            null,
            onBackPressed = {
                onBackPressed()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingsItem(
            title = "Vehicle Management",
            onClick = onVehicleManagementClicked
        )

        SettingsItem(
            title = "Export App Data",
            onClick = onExportDataClicked
        )

        SettingsItem(
            title = "Import App Data",
            onClick = {
                pickerLauncher.launch()
            }
        )
    }
}
