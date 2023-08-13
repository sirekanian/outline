package org.sirekanyan.outline

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import kotlinx.coroutines.launch
import org.sirekanyan.outline.api.OutlineApi
import org.sirekanyan.outline.api.model.AccessKey
import org.sirekanyan.outline.ui.AddKeyButton
import org.sirekanyan.outline.ui.DrawerContent
import org.sirekanyan.outline.ui.KeyContent

@Composable
fun MainContent(api: OutlineApi, state: MainState, keys: List<AccessKey>) {
    ModalNavigationDrawer({ DrawerContent(api, state) }, drawerState = state.drawer) {
        LazyColumn(contentPadding = WindowInsets.systemBars.asPaddingValues()) {
            keys.forEach { key ->
                item {
                    KeyContent(key)
                }
            }
        }
        AddKeyButton(
            visible = state.selected != null,
            onClick = {
                state.selected?.let {
                    state.scope.launch {
                        api.createAccessKey(it)
                    }
                }
            },
        )
    }
}
