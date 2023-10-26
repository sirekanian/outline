package org.sirekanyan.outline

import android.widget.Toast
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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.sirekanyan.outline.api.OutlineApi
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.ext.logError
import org.sirekanyan.outline.feature.keys.KeysErrorState
import org.sirekanyan.outline.feature.keys.KeysLoadingState
import org.sirekanyan.outline.feature.keys.KeysState
import org.sirekanyan.outline.feature.keys.KeysSuccessState
import org.sirekanyan.outline.repository.ServerRepository
import java.net.ConnectException
import java.net.UnknownHostException

@Composable
fun rememberMainState(api: OutlineApi): MainState {
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
                    Toast.makeText(context, "Check network connection", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    val supervisor = remember { SupervisorJob() }
    return remember { MainState(scope + supervisor, api) }
}

class MainState(val scope: CoroutineScope, private val api: OutlineApi) {

    val servers = ServerRepository(api)
    val drawer = DrawerState(DrawerValue.Closed)
    var page by mutableStateOf<Page>(HelloPage)
    var dialog by mutableStateOf<Dialog?>(null)
    val selectedPage by derivedStateOf { page as? SelectedPage }
    var selectedKey by mutableStateOf<Key?>(null)
    val isFabVisible by derivedStateOf { (page as? SelectedPage)?.keys is KeysSuccessState }
    var isFabLoading by mutableStateOf(false)
    var deletingKey by mutableStateOf<Key?>(null)

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
        (page as? SelectedPage)?.let { page ->
            if (showLoading) {
                page.keys = KeysLoadingState
            }
            page.keys = try {
                KeysSuccessState(api.getKeys(page.apiUrl))
            } catch (exception: Exception) {
                exception.printStackTrace()
                KeysErrorState
            }
        }
    }

}

sealed class Page

data object HelloPage : Page()

data class SelectedPage(val apiUrl: String) : Page() {
    var keys by mutableStateOf<KeysState>(KeysLoadingState)
}

sealed class Dialog

data object AddServerDialog : Dialog()

data class EditKeyDialog(val apiUrl: String, val key: Key) : Dialog()

data class DeleteKeyDialog(val apiUrl: String, val key: Key) : Dialog()

data class DeleteServerDialog(val apiUrl: String, val serverName: String) : Dialog()
