package org.sirekanyan.outline.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.sirekanyan.outline.AboutDialog
import org.sirekanyan.outline.AddServerDialog
import org.sirekanyan.outline.HelloPage
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.R
import org.sirekanyan.outline.SelectedPage
import org.sirekanyan.outline.api.model.getTotalBadgeText
import org.sirekanyan.outline.app
import org.sirekanyan.outline.ext.rememberFlowAsState
import org.sirekanyan.outline.isDebugBuild

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
        val servers by rememberFlowAsState(listOf()) { state.servers.observeServers() }
        var isCountShown by remember { mutableStateOf(false) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.outln_app_name),
                modifier = Modifier.weight(1f).padding(horizontal = 28.dp, vertical = 16.dp),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val totalBadgeText by remember {
                derivedStateOf {
                    servers.getTotalBadgeText(isCountShown)
                }
            }
            totalBadgeText?.let { badgeText ->
                TextButton({ isCountShown = !isCountShown }, Modifier.padding(end = 24.dp)) {
                    Text(badgeText)
                }
            }
        }
        if (servers.isNotEmpty()) {
            LaunchedEffect(Unit) {
                state.servers.updateServers(servers)
            }
        }
        servers.forEach { server ->
            val isSelected = state.selectedPage?.server?.id == server.id
            DrawerItem(
                icon = Icons.Default.Done,
                label = server.name,
                badge = {
                    server.getBadgeText(isCountShown)?.let { badgeText ->
                        Text(
                            text = badgeText,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                },
                selected = isSelected,
                onClick = {
                    state.page = SelectedPage(server)
                    state.closeDrawer()
                },
            )
        }
        DrawerItem(
            icon = Icons.Default.Add,
            label = stringResource(R.string.outln_drawer_add),
            onClick = {
                state.dialog = AddServerDialog
            },
        )
        Spacer(Modifier.weight(1f))
        if (servers.isNotEmpty()) {
            DrawerItem(
                icon = Icons.Default.Search,
                label = stringResource(R.string.outln_drawer_all),
                onClick = {
                    state.page = HelloPage
                    state.closeDrawer()
                },
            )
        }
        Divider(Modifier.padding(vertical = 8.dp))
        val context = LocalContext.current
        if (isDebugBuild()) {
            val debugDao = remember { context.app().debugDao }
            val scope = rememberCoroutineScope()
            DrawerItem(
                icon = Icons.Default.Warning,
                label = stringResource(R.string.outln_drawer_reset),
                onClick = {
                    scope.launch(IO) {
                        debugDao.reset()
                    }
                },
            )
        }
        DrawerItem(
            icon = Icons.Default.Info,
            label = stringResource(R.string.outln_drawer_about),
            onClick = { state.dialog = AboutDialog },
        )
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
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        badge = badge,
        modifier = Modifier.padding(horizontal = 12.dp),
        selected = selected,
        onClick = onClick,
    )
}
