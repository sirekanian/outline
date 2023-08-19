package org.sirekanyan.outline.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.sirekanyan.outline.AddServerDialog
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.R
import org.sirekanyan.outline.SelectedPage
import org.sirekanyan.outline.db.ApiUrlDao

@Composable
fun DrawerContent(dao: ApiUrlDao, state: MainState) {
    ModalDrawerSheet {
        Text(
            text = stringResource(R.string.app_name),
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
            style = MaterialTheme.typography.titleSmall,
        )
        val apiUrls by remember { dao.observeUrls() }.collectAsState(listOf())
        apiUrls.forEach { apiUrl ->
            val selected = state.selected == apiUrl
            val serverName by produceState(state.servers.getDefaultName(apiUrl), state.drawer.isOpen) {
                value = state.servers.getName(apiUrl)
            }
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Done, null) },
                label = { Text(serverName, style = MaterialTheme.typography.labelLarge) },
                modifier = Modifier.padding(horizontal = 12.dp),
                selected = selected,
                onClick = {
                    state.page = SelectedPage(apiUrl)
                    state.closeDrawer()
                },
            )
        }
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Add, null) },
            label = { Text("Add server", style = MaterialTheme.typography.labelLarge) },
            modifier = Modifier.padding(horizontal = 12.dp),
            selected = false,
            onClick = {
                state.dialog = AddServerDialog
            },
        )
    }
}
