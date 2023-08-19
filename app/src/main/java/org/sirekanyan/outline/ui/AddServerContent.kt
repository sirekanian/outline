package org.sirekanyan.outline.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.SelectedPage
import org.sirekanyan.outline.db.ApiUrlDao

@Composable
fun AddServerContent(dao: ApiUrlDao, state: MainState) {
    var draft by remember { mutableStateOf("") }
    var error by remember(draft) { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    suspend fun onAddClick() {
        try {
            isLoading = true
            state.servers.fetchName(draft)
            dao.insertUrl(draft)
            state.dialog = null
            state.page = SelectedPage(draft)
            state.closeDrawer(animated = false)
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
            onCloseClick = { state.dialog = null },
            action = "Add" to { state.scope.launch { onAddClick() } },
            isLoading = isLoading,
        )
        val focusRequester = remember { FocusRequester() }
        OutlinedTextField(
            value = draft,
            onValueChange = { draft = it.trim() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .focusRequester(focusRequester),
            label = { Text("Management API URL") },
            placeholder = { Text("https://xx.xx.xx.xx:xxx/xxxxx") },
            isError = error.isNotEmpty(),
            supportingText = { Text(error) },
            maxLines = 4,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { state.scope.launch { onAddClick() } })
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}
