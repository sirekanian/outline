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
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.sirekanyan.outline.AddServerDialog
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.R
import org.sirekanyan.outline.SelectedPage
import org.sirekanyan.outline.app
import org.sirekanyan.outline.db.DebugDao
import org.sirekanyan.outline.isDebugBuild
import org.sirekanyan.outline.isPlayFlavor
import org.sirekanyan.outline.text.formatTraffic
import org.sirekanyan.outline.ui.icons.IconOpenInNew
import org.sirekanyan.outline.ui.icons.IconPlayStore

@Composable
fun DrawerContent(state: MainState) {
    val insets = WindowInsets.systemBars.asPaddingValues()
    ModalDrawerSheet(windowInsets = WindowInsets(0.dp)) {
        DrawerSheetContent(state, insets)
    }
}

@Composable
private fun DrawerSheetContent(state: MainState, insets: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(insets)
            .padding(bottom = 8.dp),
    ) {
        Text(
            text = stringResource(R.string.outln_app_name),
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
            style = MaterialTheme.typography.titleSmall,
        )
        val serverEntities by remember { state.dao.observeUrls() }.collectAsState(listOf())
        serverEntities.forEach { serverEntity ->
            val isSelected = state.selectedPage?.server?.id == serverEntity.id
            val cachedServer = state.servers.getCachedServer(serverEntity)
            val server by produceState(cachedServer, state.drawer.isOpen) {
                value = state.servers.getServer(serverEntity)
            }
            DrawerItem(
                icon = Icons.Default.Done,
                label = server.name,
                badge = {
                    server.traffic?.let { traffic ->
                        Text(
                            text = formatTraffic(traffic),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                },
                selected = isSelected,
                onClick = {
                    state.page = SelectedPage(serverEntity)
                    state.closeDrawer()
                },
            )
        }
        DrawerItem(
            icon = Icons.Default.Add,
            label = "Add server",
            onClick = {
                state.dialog = AddServerDialog
            },
        )
        if (isPlayFlavor() || isDebugBuild()) {
            Spacer(Modifier.weight(1f))
            Divider(Modifier.padding(vertical = 8.dp))
            val context = LocalContext.current
            if (isDebugBuild()) {
                val debugDao = remember { DebugDao(context.app().database) }
                DrawerItem(
                    icon = Icons.Default.Warning,
                    label = "Reset database",
                    onClick = {
                        state.scope.launch(IO) {
                            debugDao.reset()
                        }
                    },
                )
            }
            val playUri = "https://play.google.com/store/apps/details?id=${context.packageName}"
            DrawerItem(
                icon = IconPlayStore,
                label = "Rate on Play Store",
                badge = { Icon(IconOpenInNew, null) },
                onClick = { context.startActivity(Intent(ACTION_VIEW, Uri.parse(playUri))) },
            )
        }
    }
}

@Composable
fun DrawerItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    badge: (@Composable () -> Unit)? = null,
    selected: Boolean = false,
) {
    NavigationDrawerItem(
        icon = { Icon(icon, null) },
        label = { Text(label, style = MaterialTheme.typography.labelLarge) },
        badge = badge,
        modifier = Modifier.padding(horizontal = 12.dp),
        selected = selected,
        onClick = onClick,
    )
}
