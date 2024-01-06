package org.sirekanyan.outline

import androidx.annotation.StringRes
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

data class MenuItem(@StringRes val text: Int, val icon: ImageVector, val onClick: () -> Unit)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainTopAppBar(
    title: @Composable () -> Unit,
    onMenuClick: () -> Unit,
    menuIcon: @Composable () -> Unit = { Icon(Icons.Default.Menu, "menu") },
    visibleItems: List<MenuItem> = listOf(),
    overflowItems: List<MenuItem> = listOf(),
) {
    TopAppBar(
        title = title,
        navigationIcon = { IconButton(onMenuClick, content = menuIcon) },
        actions = { MainMenu(visibleItems, overflowItems) },
        colors = TopAppBarDefaults.topAppBarColors(
            MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp).copy(alpha = 0.98f),
        ),
    )
}

@Composable
private fun MainMenu(visibleItems: List<MenuItem>, overflowItems: List<MenuItem>) {
    visibleItems.forEach { item ->
        IconButton(onClick = item.onClick) {
            Icon(item.icon, null)
        }
    }
    if (overflowItems.isNotEmpty()) {
        var isMenuVisible by remember { mutableStateOf(false) }
        IconButton({ isMenuVisible = !isMenuVisible }) {
            Icon(Icons.Default.MoreVert, null)
        }
        DropdownMenu(isMenuVisible, { isMenuVisible = false }) {
            overflowItems.forEach { (text, icon, onClick) ->
                DropdownMenuItem(
                    text = { Text(stringResource(text)) },
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
