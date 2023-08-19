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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.sirekanyan.outline.api.OutlineApi
import org.sirekanyan.outline.db.ApiUrlDao
import org.sirekanyan.outline.ext.plus
import org.sirekanyan.outline.feature.keys.KeysContent
import org.sirekanyan.outline.feature.keys.KeysErrorContent
import org.sirekanyan.outline.feature.keys.KeysErrorState
import org.sirekanyan.outline.feature.keys.KeysLoadingState
import org.sirekanyan.outline.feature.keys.KeysSuccessState
import org.sirekanyan.outline.ui.AddKeyButton
import org.sirekanyan.outline.ui.DrawerContent
import org.sirekanyan.outline.ui.KeyBottomSheet

@Composable
fun MainContent(api: OutlineApi, dao: ApiUrlDao, state: MainState) {
    ModalNavigationDrawer({ DrawerContent(dao, state) }, drawerState = state.drawer) {
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
                MainTopAppBar(stringResource(R.string.app_name)) {
                    state.openDrawer()
                }
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
                        KeysContent(insets, state, keys)
                    }
                }
                LaunchedEffect(page.selected) {
                    state.refreshCurrentKeys(showLoading = true)
                }
                val apiUrl = page.selected
                val serverName by produceState(state.servers.getDefaultName(apiUrl), apiUrl) {
                    value = state.servers.getName(apiUrl)
                }
                MainTopAppBar(serverName) {
                    state.openDrawer()
                }
            }
        }
        AddKeyButton(
            isVisible = state.isFabVisible,
            isLoading = state.isFabLoading,
            onClick = {
                state.selected?.let {
                    state.scope.launch {
                        state.isFabLoading = true
                        api.createAccessKey(it)
                        state.refreshCurrentKeys(showLoading = false)
                    }.invokeOnCompletion {
                        state.isFabLoading = false
                    }
                }
            },
        )
        state.selected?.let { selected ->
            state.selectedKey?.let { selectedKey ->
                KeyBottomSheet(
                    key = selectedKey,
                    onDismissRequest = { state.selectedKey = null },
                    onEditClick = {
                        state.dialog = EditKeyDialog(selected, selectedKey)
                    },
                    onDeleteClick = {
                        state.scope.launch {
                            api.deleteAccessKey(selected, selectedKey.accessKey.id)
                            state.refreshCurrentKeys(showLoading = false)
                        }
                    },
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MainTopAppBar(title: String, onMenuClick: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = { IconButton({ onMenuClick() }) { Icon(Icons.Default.Menu, null) } },
        colors = TopAppBarDefaults.topAppBarColors(
            MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp).copy(alpha = 0.98f),
        ),
    )
}
