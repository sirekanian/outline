package org.sirekanyan.outline.ui

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Divider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.sirekanyan.outline.AddServerDialog
import org.sirekanyan.outline.BuildConfig
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.R
import org.sirekanyan.outline.SelectedPage
import org.sirekanyan.outline.db.ApiUrlDao
import org.sirekanyan.outline.isPlayFlavor
import org.sirekanyan.outline.text.formatTraffic
import org.sirekanyan.outline.ui.icons.IconOpenInNew
import org.sirekanyan.outline.ui.icons.IconPlayStore

@Composable
fun DrawerContent(dao: ApiUrlDao, state: MainState) {
    val insets = WindowInsets.systemBars.asPaddingValues()
    ModalDrawerSheet(windowInsets = WindowInsets(0.dp)) {
        DrawerSheetContent(dao, state, insets)
    }
}

@Composable
private fun DrawerSheetContent(dao: ApiUrlDao, state: MainState, insets: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(insets)
            .padding(bottom = 8.dp),
    ) {
        Text(
            text = stringResource(R.string.app_name),
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
            style = MaterialTheme.typography.titleSmall,
        )
        val apiUrls by remember { dao.observeUrls() }.collectAsState(listOf())
        apiUrls.forEach { apiUrl ->
            val isSelected = state.selectedPage?.apiUrl == apiUrl
            val server by produceState(state.servers.getCachedServer(apiUrl), state.drawer.isOpen) {
                value = state.servers.getServer(apiUrl)
            }
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Done, null) },
                label = { Text(server.name, style = MaterialTheme.typography.labelLarge) },
                badge = {
                    server.traffic?.let { traffic ->
                        Text(
                            text = formatTraffic(traffic),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                },
                modifier = Modifier.padding(horizontal = 12.dp),
                selected = isSelected,
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
        if (isPlayFlavor() || BuildConfig.DEBUG) {
            Spacer(Modifier.weight(1f))
            Divider(Modifier.padding(vertical = 8.dp))
            val context = LocalContext.current
            val playUri = "https://play.google.com/store/apps/details?id=${context.packageName}"
            NavigationDrawerItem(
                icon = { Icon(IconPlayStore, null) },
                label = { Text("Rate on Play Store", style = MaterialTheme.typography.labelLarge) },
                badge = { Icon(IconOpenInNew, null) },
                modifier = Modifier.padding(horizontal = 12.dp),
                selected = false,
                onClick = { context.startActivity(Intent(ACTION_VIEW, Uri.parse(playUri))) },
            )
        }
    }
}
