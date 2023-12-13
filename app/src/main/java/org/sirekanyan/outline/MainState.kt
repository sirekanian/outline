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
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import org.sirekanyan.outline.api.OutlineApi
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.db.KeyDao
import org.sirekanyan.outline.db.KeyValueDao
import org.sirekanyan.outline.db.ServerDao
import org.sirekanyan.outline.db.model.ServerEntity
import org.sirekanyan.outline.db.rememberKeyDao
import org.sirekanyan.outline.db.rememberKeyValueDao
import org.sirekanyan.outline.db.rememberServerDao
import org.sirekanyan.outline.ext.logError
import org.sirekanyan.outline.ext.showToast
import org.sirekanyan.outline.feature.keys.KeysErrorState
import org.sirekanyan.outline.feature.keys.KeysIdleState
import org.sirekanyan.outline.feature.keys.KeysLoadingState
import org.sirekanyan.outline.feature.keys.KeysState
import org.sirekanyan.outline.feature.sort.Sorting
import org.sirekanyan.outline.repository.KeyRepository
import org.sirekanyan.outline.repository.ServerRepository
import java.net.ConnectException
import java.net.UnknownHostException

@Composable
fun rememberMainState(): MainState {
    val context = LocalContext.current
    val scope = rememberCoroutineScope {
        CoroutineExceptionHandler { _, throwable ->
            if (throwable is UnknownHostException) {
                logError("Uncaught exception: ${throwable.message}", throwable = null)
            } else {
                logError("Uncaught exception", throwable)
            }
            when (throwable) {
                is UnknownHostException, is ConnectException -> {
                    context.showToast("Check network connection")
                }
                else -> {
                    context.showToast("Something went wrong")
                }
            }
        }
    }
    val supervisor = remember { SupervisorJob() }
    val api = remember { OutlineApi() }
    val dao = rememberServerDao()
    val prefs = rememberKeyValueDao()
    val cache = rememberKeyDao()
    return remember { MainState(scope + supervisor, api, dao, prefs, cache) }
}

class MainState(
    val scope: CoroutineScope,
    val api: OutlineApi,
    val dao: ServerDao,
    private val prefs: KeyValueDao,
    cache: KeyDao,
) {

    val servers = ServerRepository(api, dao)
    val keys = KeyRepository(api, cache)
    val drawer = DrawerState(DrawerValue.Closed)
    var page by mutableStateOf<Page>(HelloPage)
    var dialog by mutableStateOf<Dialog?>(null)
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

}

sealed class Page

data object HelloPage : Page()

data class SelectedPage(val server: ServerEntity) : Page() {
    var keys by mutableStateOf<KeysState>(KeysIdleState)
}

sealed class Dialog

data object AddServerDialog : Dialog()

data class RenameServerDialog(val server: ServerEntity) : Dialog()

data class RenameKeyDialog(val server: ServerEntity, val key: Key) : Dialog()

data class DeleteKeyDialog(val server: ServerEntity, val key: Key) : Dialog()

data class DeleteServerDialog(val server: ServerEntity) : Dialog()
