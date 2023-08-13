package org.sirekanyan.outline.ui

import android.net.Uri
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.api.API_URLS
import org.sirekanyan.outline.api.OutlineApi

@Composable
fun DrawerContent(api: OutlineApi, state: MainState) {
    ModalDrawerSheet {
        API_URLS.forEachIndexed { index, apiUrl ->
            val selected = state.selected == index
            val serverName by produceState(Uri.parse(apiUrl).host.orEmpty()) {
                value = api.getServerName(apiUrl)
            }
            NavigationDrawerItem(
                label = { Text(serverName) },
                selected = selected,
                onClick = {
                    state.selected = index
                    state.closeDrawer()
                }
            )
        }
    }
}
