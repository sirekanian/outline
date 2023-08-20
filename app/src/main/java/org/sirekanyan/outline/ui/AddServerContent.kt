package org.sirekanyan.outline.ui

import android.widget.Toast
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
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
                .padding(16.dp, 24.dp, 16.dp, 8.dp)
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            val context = LocalContext.current
            Checkbox(
                checked = true,
                onCheckedChange = {
                    Toast.makeText(context, "Cannot be changed for now", Toast.LENGTH_SHORT).show()
                },
            )
            Text(
                text = "Trust self-signed certificates",
                modifier = Modifier.padding(end = 16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = LocalContentColor.current.copy(alpha = 0.66f),
            )
        }
    }
}
