package org.sirekanyan.outline

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class MenuItem(val text: String, val icon: ImageVector, val onClick: () -> Unit)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainTopAppBar(
    title: @Composable () -> Unit,
    onMenuClick: () -> Unit,
    menuIcon: ImageVector = Icons.Default.Menu,
    items: List<MenuItem> = listOf(),
) {
    TopAppBar(
        title = title,
        navigationIcon = { IconButton(onMenuClick) { Icon(menuIcon, null) } },
        actions = { MainMenu(items) },
        colors = TopAppBarDefaults.topAppBarColors(
            MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp).copy(alpha = 0.98f),
        ),
    )
}

@Composable
private fun MainMenu(items: List<MenuItem>) {
    if (items.isNotEmpty()) {
        var isMenuVisible by remember { mutableStateOf(false) }
        IconButton({ isMenuVisible = !isMenuVisible }) {
            Icon(Icons.Default.MoreVert, null)
        }
        DropdownMenu(isMenuVisible, { isMenuVisible = false }) {
            items.forEach { (text, icon, onClick) ->
                DropdownMenuItem(
                    text = { Text(text) },
                    leadingIcon = { Icon(icon, null) },
                    onClick = {
                        isMenuVisible = false
                        onClick()
                    },
                )
            }
        }
    }
}
