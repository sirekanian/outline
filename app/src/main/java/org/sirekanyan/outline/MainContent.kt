package org.sirekanyan.outline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.sirekanyan.outline.ext.plus
import org.sirekanyan.outline.feature.keys.KeysContent
import org.sirekanyan.outline.feature.keys.KeysErrorContent
import org.sirekanyan.outline.feature.keys.KeysErrorState
import org.sirekanyan.outline.feature.keys.KeysLoadingState
import org.sirekanyan.outline.feature.keys.KeysSuccessState
import org.sirekanyan.outline.feature.sort.SortBottomSheet
import org.sirekanyan.outline.feature.sort.Sorting
import org.sirekanyan.outline.ui.AddKeyButton
import org.sirekanyan.outline.ui.DrawerContent
import org.sirekanyan.outline.ui.KeyBottomSheet
import org.sirekanyan.outline.ui.icons.IconSort

@Composable
fun MainContent(state: MainState) {
    val sorting by state.sorting.collectAsState(Sorting.DEFAULT)
    var isSortingVisible by remember { mutableStateOf(false) }
    ModalNavigationDrawer({ DrawerContent(state) }, drawerState = state.drawer) {
        val insets = WindowInsets.systemBars.asPaddingValues() + PaddingValues(top = 64.dp)
        when (val page = state.page) {
            is HelloPage -> {
                Box(Modifier.fillMaxSize().padding(insets), Alignment.Center) {
                    TextButton(onClick = { state.dialog = AddServerDialog }) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.size(8.dp))
                        Text("Add server")
                    }
                }
                MainTopAppBar(
                    title = stringResource(R.string.outln_app_name),
                    onMenuClick = state::openDrawer,
                )
            }
            is SelectedPage -> {
                when (val keys = page.keys) {
                    is KeysLoadingState -> {
                        Box(Modifier.fillMaxSize().padding(insets), Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is KeysErrorState -> {
                        KeysErrorContent(
                            insets = insets,
                            onRetry = {
                                state.scope.launch {
                                    state.refreshCurrentKeys(showLoading = true)
                                }
                            },
                        )
                    }
                    is KeysSuccessState -> {
                        KeysContent(insets, state, keys, sorting)
                    }
                }
                LaunchedEffect(page.server) {
                    state.refreshCurrentKeys(showLoading = true)
                }
                val serverEntity = page.server
                val cachedServer = state.servers.getCachedServer(serverEntity)
                val server by produceState(cachedServer, serverEntity) {
                    value = state.servers.getServer(serverEntity)
                }
                MainTopAppBar(
                    title = server.name,
                    onMenuClick = state::openDrawer,
                    items = listOf(
                        MenuItem("Sort by…", IconSort) {
                            isSortingVisible = true
                        },
                        MenuItem("Delete", Icons.Default.Delete) {
                            state.dialog = DeleteServerDialog(page.server, server.name)
                        },
                    ),
                )
            }
        }
        AddKeyButton(
            isVisible = state.isFabVisible,
            isLoading = state.isFabLoading,
            onClick = {
                state.selectedPage?.let { page ->
                    state.scope.launch {
                        state.isFabLoading = true
                        state.api.createAccessKey(page.server)
                        state.refreshCurrentKeys(showLoading = false)
                    }.invokeOnCompletion {
                        state.isFabLoading = false
                    }
                }
            },
        )
        state.selectedPage?.let { page ->
            state.selectedKey?.let { key ->
                KeyBottomSheet(
                    key = key,
                    onDismissRequest = { state.selectedKey = null },
                    onEditClick = { state.dialog = EditKeyDialog(page.server, key) },
                    onDeleteClick = { state.dialog = DeleteKeyDialog(page.server, key) },
                )
            }
        }
        if (isSortingVisible) {
            SortBottomSheet(
                sorting = sorting,
                onSortingChange = { state.putSorting(it) },
                onDismissRequest = { isSortingVisible = false },
            )
        }
    }
}
