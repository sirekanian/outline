package org.sirekanyan.outline.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.sirekanyan.outline.NotSupportedContent
import org.sirekanyan.outline.Router
import org.sirekanyan.outline.SelectedPage
import org.sirekanyan.outline.api.model.createServerEntity
import org.sirekanyan.outline.app
import org.sirekanyan.outline.ext.rememberStateScope
import org.sirekanyan.outline.repository.ServerRepository
import javax.net.ssl.SSLException

@Composable
private fun rememberAddServerState(router: Router): AddServerState {
    val context = LocalContext.current
    val scope = rememberStateScope()
    val servers = remember { context.app().serverRepository }
    val draft = rememberSaveable { mutableStateOf("") }
    val insecure = rememberSaveable { mutableStateOf(false) }
    return remember { AddServerState(scope, router, servers, draft, insecure) }
}

private class AddServerState(
    private val scope: CoroutineScope,
    private val router: Router,
    private val servers: ServerRepository,
    draftState: MutableState<String>,
    insecureState: MutableState<Boolean>,
) {

    var draft by draftState
    var insecure by insecureState
    var error by mutableStateOf("")
    var isLoading by mutableStateOf(false)
        private set
    var isDialogVisible by mutableStateOf(false)

    fun onAddClicked() {
        if (draft.startsWith("ss://")) {
            isDialogVisible = true
            return
        }
        scope.launch {
            updateServer()
        }
    }

    private suspend fun updateServer() {
        try {
            isLoading = true
            val server = servers.updateServer(createServerEntity(draft, insecure))
            router.dialog = null
            router.page = SelectedPage(server)
            router.closeDrawer(animated = false)
        } catch (exception: SSLException) {
            exception.printStackTrace()
            error = "Cannot establish a secure connection"
        } catch (exception: Exception) {
            exception.printStackTrace()
            error = "Check URL or try again"
        } finally {
            isLoading = false
        }
    }

}

@Composable
fun AddServerContent(router: Router) {
    val state = rememberAddServerState(router)
    Column {
        DialogToolbar(
            title = "Add server",
            onCloseClick = { router.dialog = null },
            action = "Add" to { state.onAddClicked() },
            isLoading = state.isLoading,
        )
        val focusRequester = remember { FocusRequester() }
        OutlinedTextField(
            value = state.draft,
            onValueChange = {
                state.draft = it.trim()
                state.error = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 24.dp, 16.dp, 8.dp)
                .focusRequester(focusRequester),
            label = { Text("Management API URL") },
            placeholder = { Text("https://xx.xx.xx.xx:xxx/xxxxx", Modifier.alpha(0.38f)) },
            isError = state.error.isNotEmpty(),
            supportingText = { Text(state.error) },
            maxLines = 4,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { state.onAddClicked() }),
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = state.insecure,
                onCheckedChange = { state.insecure = it },
            )
            Text(
                text = "Allow insecure connection",
                modifier = Modifier.padding(end = 16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = LocalContentColor.current.copy(alpha = 0.66f),
            )
        }
    }
    if (state.isDialogVisible) {
        NotSupportedContent(onDismissRequest = { state.isDialogVisible = false })
    }
}
