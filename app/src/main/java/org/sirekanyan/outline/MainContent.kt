package org.sirekanyan.outline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.sirekanyan.outline.api.OutlineApi
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.db.ApiUrlDao
import org.sirekanyan.outline.ui.AddKeyButton
import org.sirekanyan.outline.ui.DrawerContent
import org.sirekanyan.outline.ui.KeyBottomSheet
import org.sirekanyan.outline.ui.KeyContent

@Composable
fun MainContent(api: OutlineApi, dao: ApiUrlDao, state: MainState, keys: List<Key>) {
    ModalNavigationDrawer({ DrawerContent(api, dao, state) }, drawerState = state.drawer) {
        if (state.page is HelloPage) {
            Column {
                MainTopAppBar {
                    state.openDrawer()
                }
                Box(Modifier.fillMaxSize().navigationBarsPadding(), Alignment.Center) {
                    TextButton(onClick = { state.page = DraftPage }) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.size(8.dp))
                        Text("Add server")
                    }
                }
            }
        } else {
            LazyColumn(contentPadding = WindowInsets.systemBars.asPaddingValues()) {
                keys.sortedByDescending(Key::traffic).forEach { key ->
                    item {
                        KeyContent(key, onClick = { state.selectedKey = key })
                    }
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
        state.selected?.let { selected ->
            state.selectedKey?.let { selectedKey ->
                KeyBottomSheet(
                    key = selectedKey,
                    onDismissRequest = { state.selectedKey = null },
                    onDeleteClick = {
                        state.scope.launch {
                            api.deleteAccessKey(selected, selectedKey.accessKey.id)
                        }
                    },
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MainTopAppBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        navigationIcon = { IconButton({ onMenuClick() }) { Icon(Icons.Default.Menu, null) } },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
        ),
    )
}
