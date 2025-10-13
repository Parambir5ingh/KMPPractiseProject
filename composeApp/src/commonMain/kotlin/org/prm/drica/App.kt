package org.prm.drica

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import drica.composeapp.generated.resources.Res
import drica.composeapp.generated.resources.ic_add
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.prm.drica.ui.theme.ScreenBackgroundColor

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        var inputText by rememberSaveable { mutableStateOf("") }
        val dataList = remember { mutableStateListOf<String>() }

        // Add items dynamically
        LaunchedEffect(Unit) {
            repeat(100) {
                dataList.add("Item #$it")
            }
        }

        Column(
            modifier = Modifier
                .background(ScreenBackgroundColor)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Box(modifier = Modifier.fillMaxSize()) {

                HomeList(dataList)

                // ADD button
                FloatingActionButton(
                    onClick = { dataList.add(0, "Item #${dataList.size + 1}") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(25.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_add),
                        contentDescription = "Add",
                    )
                }
            }
        }
    }
}