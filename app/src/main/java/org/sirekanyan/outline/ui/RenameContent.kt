package org.sirekanyan.outline.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.ext.rememberStateScope

@Composable
fun RenameContent(
    state: MainState,
    dialogTitle: String,
    initialName: String,
    defaultName: String,
    onSaveClicked: suspend (String) -> Unit,
) {
    val scope = rememberStateScope()
    var draft by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initialName, TextRange(Int.MAX_VALUE)))
    }
    var error by remember(draft) { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    Column {
        DialogToolbar(
            title = dialogTitle,
            onCloseClick = { state.dialog = null },
            action = "Save" to {
                scope.launch {
                    try {
                        isLoading = true
                        val newName = draft.text.ifBlank { defaultName }
                        onSaveClicked(newName)
                        state.dialog = null
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        error = "Check name or try again"
                    } finally {
                        isLoading = false
                    }
                }
            },
            isLoading = isLoading,
        )
        val focusRequester = remember { FocusRequester() }
        OutlinedTextField(
            value = draft,
            onValueChange = { draft = it.copy(text = it.text.trim('\n')) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .focusRequester(focusRequester),
            label = { Text("Name") },
            placeholder = { Text(defaultName) },
            isError = error.isNotEmpty(),
            supportingText = { Text(error) },
            maxLines = 4,
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}
