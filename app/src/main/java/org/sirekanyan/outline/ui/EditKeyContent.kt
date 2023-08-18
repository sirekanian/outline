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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.sirekanyan.outline.EditKeyDialog
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.api.OutlineApi

@Composable
fun EditKeyContent(api: OutlineApi, state: MainState, dialog: EditKeyDialog) {
    val accessKey = dialog.key.accessKey
    var draft by remember {
        mutableStateOf(TextFieldValue(accessKey.nameOrDefault, TextRange(Int.MAX_VALUE)))
    }
    var error by remember(draft) {
        mutableStateOf("")
    }
    Column {
        DialogToolbar(
            title = "Edit key",
            onCloseClick = { state.dialog = null },
            action = "Save" to {
                state.scope.launch {
                    try {
                        val newName = draft.text.ifBlank { accessKey.defaultName }
                        api.renameAccessKey(dialog.selected, accessKey.id, newName)
                        state.dialog = null
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        error = "Check name or try again"
                    }
                }
            },
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
            placeholder = { Text(accessKey.defaultName) },
            isError = error.isNotEmpty(),
            supportingText = { Text(error) },
            maxLines = 4,
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}
