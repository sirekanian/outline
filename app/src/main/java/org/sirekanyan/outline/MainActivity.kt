package org.sirekanyan.outline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import kotlinx.coroutines.launch
import org.sirekanyan.outline.api.OutlineApi
import org.sirekanyan.outline.db.rememberApiUrlDao
import org.sirekanyan.outline.ui.AddServerContent
import org.sirekanyan.outline.ui.DeleteKeyContent
import org.sirekanyan.outline.ui.EditKeyContent
import org.sirekanyan.outline.ui.theme.OutlineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val api = remember { OutlineApi() }
            val dao = rememberApiUrlDao()
            val state = rememberMainState(api)
            OutlineTheme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    BackHandler(state.drawer.isOpen) {
                        state.closeDrawer()
                    }
                    MainContent(api, dao, state)
                    state.dialog?.let { dialog ->
                        BackHandler(state.dialog != null) {
                            state.dialog = null
                        }
                        when (dialog) {
                            is AddServerDialog -> Surface { AddServerContent(dao, state) }
                            is EditKeyDialog -> Surface { EditKeyContent(api, state, dialog) }
                            is DeleteKeyDialog -> {
                                val (apiUrl, key) = dialog
                                DeleteKeyContent(
                                    key = key,
                                    onDismiss = { state.dialog = null },
                                    onConfirm = {
                                        state.scope.launch {
                                            api.deleteAccessKey(apiUrl, key.accessKey.id)
                                            state.refreshCurrentKeys(showLoading = false)
                                        }
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
