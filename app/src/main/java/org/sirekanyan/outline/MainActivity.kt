package org.sirekanyan.outline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import org.sirekanyan.outline.api.OutlineApi
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.db.rememberApiUrlDao
import org.sirekanyan.outline.ui.AddServerContent
import org.sirekanyan.outline.ui.EditKeyContent
import org.sirekanyan.outline.ui.theme.OutlineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val api = remember { OutlineApi() }
            val dao = rememberApiUrlDao()
            val state = rememberMainState()
            val keys by produceState(listOf<Key>(), state.selected) {
                value = state.selected?.let { api.getKeys(it) } ?: listOf()
            }
            OutlineTheme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    BackHandler(state.drawer.isOpen) {
                        state.closeDrawer()
                    }
                    MainContent(api, dao, state, keys)
                    state.dialog?.let { dialog ->
                        BackHandler(state.dialog != null) {
                            state.dialog = null
                        }
                        Surface {
                            when (dialog) {
                                is AddServerDialog -> AddServerContent(api, dao, state)
                                is EditKeyDialog -> EditKeyContent(api, state, dialog)
                            }
                        }
                    }
                }
            }
        }
    }
}
