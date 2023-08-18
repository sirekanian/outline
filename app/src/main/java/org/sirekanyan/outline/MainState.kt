package org.sirekanyan.outline

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.sirekanyan.outline.api.OutlineApi
import org.sirekanyan.outline.api.model.Key

@Composable
fun rememberMainState(api: OutlineApi): MainState {
    val scope = rememberCoroutineScope()
    return remember { MainState(scope, api) }
}

class MainState(val scope: CoroutineScope, private val api: OutlineApi) {

    val drawer = DrawerState(DrawerValue.Closed)
    var page by mutableStateOf<Page>(HelloPage)
    var dialog by mutableStateOf<Dialog?>(null)
    val selected by derivedStateOf { (page as? SelectedPage)?.selected }
    var selectedKey by mutableStateOf<Key?>(null)

    fun openDrawer() {
        scope.launch {
            drawer.open()
        }
    }

    fun closeDrawer(animated: Boolean = true) {
        scope.launch {
            if (animated) {
                drawer.close()
            } else {
                drawer.snapTo(DrawerValue.Closed)
            }
        }
    }

    suspend fun refreshCurrentKeys() {
        (page as? SelectedPage)?.let { page ->
            page.keys = api.getKeys(page.selected)
        }
    }

}

sealed class Page

data object HelloPage : Page()

data class SelectedPage(val selected: String) : Page() {
    var keys by mutableStateOf(listOf<Key>())
}

sealed class Dialog

data object AddServerDialog : Dialog()

data class EditKeyDialog(val selected: String, val key: Key) : Dialog()
