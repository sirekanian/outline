package org.sirekanyan.outline

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import org.sirekanyan.outline.api.model.AccessKey
import org.sirekanyan.outline.ui.DrawerContent
import org.sirekanyan.outline.ui.KeyContent

@Composable
fun MainContent(state: MainState, keys: List<AccessKey>) {
    ModalNavigationDrawer(drawerContent = { DrawerContent(state) }, drawerState = state.drawer) {
        LazyColumn {
            keys.forEach { key ->
                item {
                    KeyContent(key)
                }
            }
        }
    }
}