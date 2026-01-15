package org.prm.drica.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/*
* Created by parambirsingh ON 15/01/26
*/
@Composable
fun GenericOptionsMenu(menuItems : ArrayList<MenuItem>) {
    var showMenu by remember { mutableStateOf(false) }

    if (menuItems.isNotEmpty()) {
        Box(modifier = Modifier.wrapContentWidth().wrapContentHeight()) {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Options"
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                menuItems.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.title) },
                        onClick = {
                            showMenu = false
                            item.onClick()
                        }
                    )
                }
            }
        }
    }
}

data class MenuItem(
    val title: String,
    val onClick: () -> Unit
)