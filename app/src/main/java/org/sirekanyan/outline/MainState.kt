package org.sirekanyan.outline

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.api.model.Server
import org.sirekanyan.outline.db.KeyValueDao
import org.sirekanyan.outline.ext.asyncAll
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
import kotlin.time.Duration.Companion.seconds

@Composable
fun rememberMainState(router: Router): MainState {
    val context = LocalContext.current
    val scope = rememberStateScope()
    val search = rememberSearchState()
    val prefs = remember { context.app().prefsDao }
    val servers = remember { context.app().serverRepository }
    val keys = remember { context.app().keyRepository }
    return remember { MainState(scope, servers, keys, search, router, prefs) }
}

class MainState(
    scope: CoroutineScope,
    val servers: ServerRepository,
    val keys: KeyRepository,
    val search: SearchState,
    private val router: Router,
    private val prefs: KeyValueDao,
) : CoroutineScope by scope {

    val drawer = router.drawer
    val drawerDisabled by derivedStateOf { search.isOpened && drawer.isClosed }
    var page by router.pageState
    var dialog by router.dialogState
    val selectedPage by derivedStateOf { page as? SelectedPage }
    var selectedKey by mutableStateOf<Key?>(null)
    val isFabVisible by derivedStateOf { (page as? SelectedPage)?.keys is KeysIdleState }
    var isFabLoading by mutableStateOf(false)
    var deletingKey by mutableStateOf<Key?>(null)
    val sorting = prefs.observe(Sorting.KEY).map(Sorting::getByKey)

    fun putSorting(sorting: Sorting) {
        launch {
            prefs.put(Sorting.KEY, sorting.key)
        }
    }

    fun openDrawer() {
        router.openDrawer()
    }

    fun closeDrawer(animated: Boolean = true) {
        router.closeDrawer(animated)
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

    private suspend fun refreshAllKeys() {
        val page = page as? HelloPage ?: return
        if (page.keys is KeysLoadingState) return
        withContext(Dispatchers.IO) {
            page.keys = KeysLoadingState
            page.keys = try {
                servers.getServers()
                    .asyncAll { server ->
                        runCatching {
                            withTimeout(5.seconds) {
                                keys.updateKeys(server)
                            }
                        }
                    }
                KeysIdleState
            } catch (exception: Exception) {
                exception.printStackTrace()
                KeysErrorState
            }
        }
    }

    fun onRetryButtonClicked() {
        launch {
            refreshCurrentKeys(showLoading = true)
        }
    }

    fun onUpdateButtonClicked() {
        launch {
            refreshAllKeys()
        }
    }

    fun onAddKeyClicked() {
        selectedPage?.let { page ->
            launch {
                isFabLoading = true
                keys.createKey(page.server)
                refreshCurrentKeys(showLoading = false)
            }.invokeOnCompletion {
                isFabLoading = false
            }
        }
    }

    fun onDeleteKeyConfirmed(key: Key) {
        launch {
            deletingKey = key
            keys.deleteKey(key)
            try {
                keys.updateKeys(key.server)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }.invokeOnCompletion {
            deletingKey = null
        }
    }

    fun onDeleteServerConfirmed(server: Server) {
        launch {
            servers.deleteServer(server)
        }
        page = HelloPage
        openDrawer()
    }

}

@Parcelize
sealed class Page : Parcelable

data object HelloPage : Page() {
    @IgnoredOnParcel
    var keys by mutableStateOf<KeysState>(KeysIdleState)
}

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

data object AboutDialog : Dialog()
