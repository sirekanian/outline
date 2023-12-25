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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.NotSupportedContent
import org.sirekanyan.outline.Router
import org.sirekanyan.outline.SelectedPage
import org.sirekanyan.outline.api.model.createServerEntity
import org.sirekanyan.outline.ext.rememberStateScope
import javax.net.ssl.SSLException

@Composable
fun AddServerContent(state: MainState, router: Router) {
    val scope = rememberStateScope()
    var draft by rememberSaveable { mutableStateOf("") }
    var insecure by rememberSaveable { mutableStateOf(false) }
    var error by remember(draft) { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isDialogVisible by remember { mutableStateOf(false) }
    suspend fun onAddClick() {
        if (draft.startsWith("ss://")) {
            isDialogVisible = true
            return
        }
        try {
            isLoading = true
            val server = state.servers.updateServer(createServerEntity(draft, insecure))
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
    Column {
        DialogToolbar(
            title = "Add server",
            onCloseClick = { router.dialog = null },
            action = "Add" to { scope.launch { onAddClick() } },
            isLoading = isLoading,
        )
        val focusRequester = remember { FocusRequester() }
        OutlinedTextField(
            value = draft,
            onValueChange = { draft = it.trim() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 24.dp, 16.dp, 8.dp)
                .focusRequester(focusRequester),
            label = { Text("Management API URL") },
            placeholder = { Text("https://xx.xx.xx.xx:xxx/xxxxx") },
            isError = error.isNotEmpty(),
            supportingText = { Text(error) },
            maxLines = 4,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { scope.launch { onAddClick() } })
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = insecure,
                onCheckedChange = { insecure = it },
            )
            Text(
                text = "Allow insecure connection",
                modifier = Modifier.padding(end = 16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = LocalContentColor.current.copy(alpha = 0.66f),
            )
        }
    }
    if (isDialogVisible) {
        NotSupportedContent(onDismissRequest = { isDialogVisible = false })
    }
}
