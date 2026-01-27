package org.prm.drica.navigation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.prm.drica.ui.TitleBar

@Composable
fun SettingsScreen(
    onBackPressed: () -> Unit,
    onExportDataClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // 🔹 Title Bar
        TitleBar(
            title = "Settings",
            null,
            onBackPressed = {
                onBackPressed()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Settings Options
        SettingsItem(
            title = "Export App Data",
            onClick = onExportDataClicked
        )
    }
}
