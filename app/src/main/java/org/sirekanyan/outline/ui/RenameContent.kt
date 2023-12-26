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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.sirekanyan.outline.Router
import org.sirekanyan.outline.ext.rememberStateScope

interface RenameDelegate {
    suspend fun onRename(newName: String)
}

@Composable
fun rememberRenameState(router: Router, delegate: RenameDelegate): RenameState {
    val scope = rememberStateScope()
    return remember { RenameState(scope, router, delegate) }
}

class RenameState(
    scope: CoroutineScope,
    private val router: Router,
    private val renameDelegate: RenameDelegate,
) : CoroutineScope by scope {

    var error by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    fun onSaveClicked(newName: String) {
        launch {
            try {
                isLoading = true
                renameDelegate.onRename(newName)
                router.dialog = null
            } catch (exception: Exception) {
                exception.printStackTrace()
                error = "Check name or try again"
            } finally {
                isLoading = false
            }
        }
    }

}

@Composable
fun RenameContent(
    state: RenameState,
    router: Router,
    dialogTitle: String,
    initialName: String,
    defaultName: String,
) {
    var draft by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initialName, TextRange(Int.MAX_VALUE)))
    }
    Column {
        DialogToolbar(
            title = dialogTitle,
            onCloseClick = { router.dialog = null },
            action = "Save" to {
                val newName = draft.text.ifBlank { defaultName }
                state.onSaveClicked(newName)
            },
            isLoading = state.isLoading,
        )
        val focusRequester = remember { FocusRequester() }
        OutlinedTextField(
            value = draft,
            onValueChange = {
                draft = it.copy(text = it.text.trim('\n'))
                state.error = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .focusRequester(focusRequester),
            label = { Text("Name") },
            placeholder = { Text(defaultName) },
            isError = state.error.isNotEmpty(),
            supportingText = { Text(state.error) },
            maxLines = 4,
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}
