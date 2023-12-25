package org.sirekanyan.outline

import android.os.Parcelable
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.api.model.Server
import org.sirekanyan.outline.db.KeyValueDao
import org.sirekanyan.outline.ext.rememberStateScope
import org.sirekanyan.outline.feature.keys.KeysErrorState
import org.sirekanyan.outline.feature.keys.KeysIdleState
import org.sirekanyan.outline.feature.keys.KeysLoadingState
import org.sirekanyan.outline.feature.keys.KeysState
import org.sirekanyan.outline.feature.sort.Sorting
import org.sirekanyan.outline.repository.KeyRepository
import org.sirekanyan.outline.repository.ServerRepository
import org.sirekanyan.outline.ui.SearchState
import org.sirekanyan.outline.ui.rememberSearchState

@Composable
fun rememberMainState(): MainState {
    val context = LocalContext.current
    val scope = rememberStateScope()
    val search = rememberSearchState()
    val page = rememberSaveable { mutableStateOf<Page>(HelloPage) }
    val dialog = rememberSaveable { mutableStateOf<Dialog?>(null) }
    val prefs = remember { context.app().prefsDao }
    val servers = remember { context.app().serverRepository }
    val keys = remember { context.app().keyRepository }
    return remember { MainState(scope, servers, keys, search, page, dialog, prefs) }
}

class MainState(
    val scope: CoroutineScope,
    val servers: ServerRepository,
    val keys: KeyRepository,
    val search: SearchState,
    pageState: MutableState<Page>,
    dialogState: MutableState<Dialog?>,
    private val prefs: KeyValueDao,
) {

    val drawer = DrawerState(DrawerValue.Closed)
    val drawerDisabled by derivedStateOf { search.isOpened && drawer.isClosed }
    var page by pageState
    var dialog by dialogState
    val selectedPage by derivedStateOf { page as? SelectedPage }
    var selectedKey by mutableStateOf<Key?>(null)
    val isFabVisible by derivedStateOf { (page as? SelectedPage)?.keys is KeysIdleState }
    var isFabLoading by mutableStateOf(false)
    var deletingKey by mutableStateOf<Key?>(null)
    val sorting = prefs.observe(Sorting.KEY).map(Sorting::getByKey)

    fun putSorting(sorting: Sorting) {
        scope.launch {
            prefs.put(Sorting.KEY, sorting.key)
        }
    }

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

    suspend fun refreshCurrentKeys(showLoading: Boolean) {
        val page = page as? SelectedPage ?: return
        withContext(Dispatchers.IO) {
            if (showLoading) {
                page.keys = KeysLoadingState
            }
            page.keys = try {
                keys.updateKeys(page.server)
                KeysIdleState
            } catch (exception: Exception) {
                exception.printStackTrace()
                KeysErrorState
            }
        }
    }

    suspend fun refreshHelloPage(server: Server) {
        if (page !is HelloPage) return
        withContext(Dispatchers.IO) {
            try {
                keys.updateKeys(server)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    fun onRetryButtonClicked() {
        scope.launch {
            refreshCurrentKeys(showLoading = true)
        }
    }

    fun onAddKeyClicked() {
        selectedPage?.let { page ->
            scope.launch {
                isFabLoading = true
                keys.createKey(page.server)
                refreshCurrentKeys(showLoading = false)
            }.invokeOnCompletion {
                isFabLoading = false
            }
        }
    }

    fun onDeleteKeyConfirmed(key: Key) {
        scope.launch {
            deletingKey = key
            keys.deleteKey(key)
            refreshCurrentKeys(showLoading = false)
            refreshHelloPage(key.server)
        }.invokeOnCompletion {
            deletingKey = null
        }
    }

    fun onDeleteServerConfirmed(server: Server) {
        scope.launch(Dispatchers.IO) {
            servers.deleteServer(server)
        }
        page = HelloPage
        openDrawer()
    }

}

@Parcelize
sealed class Page : Parcelable

data object HelloPage : Page()

data class SelectedPage(val server: Server) : Page() {
    @IgnoredOnParcel
    var keys by mutableStateOf<KeysState>(KeysIdleState)
}

@Parcelize
sealed class Dialog : Parcelable

data object AddServerDialog : Dialog()

data class RenameServerDialog(val server: Server) : Dialog()

data class RenameKeyDialog(val key: Key) : Dialog()

data class DeleteKeyDialog(val key: Key) : Dialog()

data class DeleteServerDialog(val server: Server) : Dialog()
