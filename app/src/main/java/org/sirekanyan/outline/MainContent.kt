package org.sirekanyan.outline

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.sirekanyan.outline.ext.plus
import org.sirekanyan.outline.ext.rememberFlowAsState
import org.sirekanyan.outline.ext.showToast
import org.sirekanyan.outline.feature.keys.KeysContent
import org.sirekanyan.outline.feature.keys.KeysErrorContent
import org.sirekanyan.outline.feature.keys.KeysErrorState
import org.sirekanyan.outline.feature.keys.KeysIdleState
import org.sirekanyan.outline.feature.keys.KeysLoadingState
import org.sirekanyan.outline.feature.sort.SortBottomSheet
import org.sirekanyan.outline.feature.sort.Sorting
import org.sirekanyan.outline.ui.AddKeyButton
import org.sirekanyan.outline.ui.DrawerContent
import org.sirekanyan.outline.ui.KeyBottomSheet
import org.sirekanyan.outline.ui.SearchField
import org.sirekanyan.outline.ui.icons.IconSort

@Composable
fun MainContent(state: MainState) {
    val sorting by state.sorting.collectAsState(Sorting.DEFAULT)
    var isSortingVisible by remember { mutableStateOf(false) }
    ModalNavigationDrawer({ DrawerContent(state) }, Modifier, state.drawer, !state.drawerDisabled) {
        val insets = WindowInsets.systemBars.asPaddingValues() + PaddingValues(top = 64.dp)
        when (val page = state.page) {
            is HelloPage -> {
                val search = state.search
                BackHandler(search.isOpened) {
                    search.closeSearch()
                }
                val allKeys by rememberFlowAsState(initial = null, search.query) {
                    state.keys.observeAllKeys(search.query)
                }
                allKeys?.let { keys ->
                    if (keys.isNotEmpty()) {
                        KeysContent(insets, state, keys, sorting)
                    } else if (!search.isOpened) {
                        Box(Modifier.fillMaxSize().padding(insets), Alignment.Center) {
                            TextButton(onClick = { state.dialog = AddServerDialog }) {
                                Icon(Icons.Default.Add, null)
                                Spacer(Modifier.size(8.dp))
                                Text(stringResource(R.string.outln_text_add_server))
                            }
                        }
                    }
                }
                if (search.isOpened) {
                    MainTopAppBar(
                        title = { SearchField(search.query) { search.query = it } },
                        onMenuClick = search::closeSearch,
                        menuIcon = { Icon(Icons.Default.ArrowBack, "back") },
                    )
                } else {
                    val menuItems: List<MenuItem> =
                        if (allKeys.isNullOrEmpty()) {
                            listOf()
                        } else {
                            listOf(
                                MenuItem(R.string.outln_menu_sort, IconSort) {
                                    isSortingVisible = true
                                },
                                MenuItem(R.string.outln_menu_search, Icons.Default.Search) {
                                    search.openSearch()
                                },
                            )
                        }
                    MainTopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.outln_app_name),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        onMenuClick = state::openDrawer,
                        visibleItems = menuItems,
                    )
                }
            }
            is SelectedPage -> {
                val keys by rememberFlowAsState(listOf(), page.server.id) {
                    state.keys.observeKeys(page.server)
                }
                KeysContent(insets + PaddingValues(bottom = 88.dp), state, keys, sorting)
                val hasKeys = keys.isNotEmpty()
                Box(Modifier.fillMaxSize().padding(insets).alpha(0.95f)) {
                    AnimatedVisibility(
                        visible = page.keys is KeysLoadingState && hasKeys,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                    ) {
                        LinearProgressIndicator(Modifier.fillMaxWidth())
                    }
                }
                Box(Modifier.fillMaxSize().padding(insets), Alignment.Center) {
                    AnimatedVisibility(
                        visible = page.keys is KeysLoadingState && !hasKeys,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        CircularProgressIndicator()
                    }
                }
                when (page.keys) {
                    is KeysIdleState -> {
                        // nothing
                    }
                    is KeysLoadingState -> {
                        // nothing
                    }
                    is KeysErrorState -> {
                        if (hasKeys) {
                            val context = LocalContext.current
                            LaunchedEffect(Unit) {
                                context.showToast(R.string.outln_error_check_network)
                            }
                        } else {
                            KeysErrorContent(
                                insets = insets,
                                onRetry = { state.onRetryButtonClicked() },
                            )
                        }
                    }
                }
                LaunchedEffect(page.server) {
                    state.refreshCurrentKeys(showLoading = true)
                }
                MainTopAppBar(
                    title = {
                        Text(
                            text = page.server.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    onMenuClick = state::openDrawer,
                    visibleItems = listOf(
                        MenuItem(R.string.outln_menu_sort, IconSort) {
                            isSortingVisible = true
                        },
                    ),
                    overflowItems = listOf(
                        MenuItem(R.string.outln_menu_edit, Icons.Default.Edit) {
                            state.dialog = RenameServerDialog(page.server)
                        },
                        MenuItem(R.string.outln_menu_delete, Icons.Default.Delete) {
                            state.dialog = DeleteServerDialog(page.server)
                        },
                    ),
                )
            }
        }
        AddKeyButton(
            isVisible = state.isFabVisible,
            isLoading = state.isFabLoading,
            onClick = { state.onAddKeyClicked() },
        )
        state.selectedKey?.let { key ->
            KeyBottomSheet(
                key = key,
                onDismissRequest = { state.selectedKey = null },
                onEditClick = { state.dialog = RenameKeyDialog(key) },
                onDeleteClick = { state.dialog = DeleteKeyDialog(key) },
            )
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
