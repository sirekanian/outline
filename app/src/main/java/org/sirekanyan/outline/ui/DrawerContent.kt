package org.sirekanyan.outline.ui

import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.api.API_URLS

@Composable
fun DrawerContent(state: MainState) {
    val scope = rememberCoroutineScope()
    ModalDrawerSheet {
        API_URLS.forEachIndexed { index, (name) ->
            val selected = state.selected == index
            NavigationDrawerItem(
                label = { Text(name) },
                selected = selected,
                onClick = {
                    state.selected = index
                    scope.launch { state.drawer.close() }
                }
            )
        }
    }
}