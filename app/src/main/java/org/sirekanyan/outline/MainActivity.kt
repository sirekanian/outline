package org.sirekanyan.outline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import kotlinx.coroutines.launch
import org.sirekanyan.outline.ui.AddServerContent
import org.sirekanyan.outline.ui.DeleteKeyContent
import org.sirekanyan.outline.ui.DeleteServerContent
import org.sirekanyan.outline.ui.RenameKeyContent
import org.sirekanyan.outline.ui.RenameServerContent
import org.sirekanyan.outline.ui.theme.OutlineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val state = rememberMainState()
            OutlineTheme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    BackHandler(state.drawer.isOpen) {
                        state.closeDrawer()
                    }
                    MainContent(state)
                    state.dialog?.let { dialog ->
                        BackHandler(state.dialog != null) {
                            state.dialog = null
                        }
                        when (dialog) {
                            is AddServerDialog -> Surface { AddServerContent(state) }
                            is RenameServerDialog -> Surface { RenameServerContent(state, dialog) }
                            is RenameKeyDialog -> Surface { RenameKeyContent(state, dialog) }
                            is DeleteKeyDialog -> {
                                val (server, key) = dialog
                                DeleteKeyContent(
                                    key = key,
                                    onDismiss = { state.dialog = null },
                                    onConfirm = {
                                        state.scope.launch {
                                            state.deletingKey = key
                                            state.api.deleteAccessKey(server, key.accessKey.id)
                                            state.refreshCurrentKeys(showLoading = false)
                                        }.invokeOnCompletion {
                                            state.deletingKey = null
                                        }
                                    }
                                )
                            }
                            is DeleteServerDialog -> {
                                val (server, serverName) = dialog
                                DeleteServerContent(
                                    serverName = serverName,
                                    onDismiss = { state.dialog = null },
                                    onConfirm = {
                                        state.scope.launch {
                                            state.dao.deleteUrl(server.id)
                                        }
                                        state.page = HelloPage
                                        state.openDrawer()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
