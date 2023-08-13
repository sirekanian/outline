package org.sirekanyan.outline.ui

import android.net.Uri
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
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.R
import org.sirekanyan.outline.api.OutlineApi
import org.sirekanyan.outline.db.rememberApiUrlDao

@Composable
fun DrawerContent(api: OutlineApi, state: MainState) {
    val dao = rememberApiUrlDao()
    ModalDrawerSheet {
        Text(
            text = stringResource(R.string.app_name),
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
            style = MaterialTheme.typography.titleSmall,
        )
        val apiUrls by remember { dao.observeUrls() }.collectAsState(listOf())
        apiUrls.forEach { apiUrl ->
            val selected = state.selected == apiUrl
            val serverName by produceState(Uri.parse(apiUrl).host.orEmpty()) {
                value = api.getServerName(apiUrl)
            }
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Done, null) },
                label = { Text(serverName, style = MaterialTheme.typography.labelLarge) },
                modifier = Modifier.padding(horizontal = 12.dp),
                selected = selected,
                onClick = {
                    state.selected = apiUrl
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
                // TODO: do something useful
                state.selected = null
                state.closeDrawer()
            },
        )
    }
}
