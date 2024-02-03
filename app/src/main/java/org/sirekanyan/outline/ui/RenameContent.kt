package org.sirekanyan.outline.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.sirekanyan.outline.R
import org.sirekanyan.outline.Res
import org.sirekanyan.outline.Router
import org.sirekanyan.outline.ext.rememberStateScope
import org.sirekanyan.outline.rememberResources

interface RenameDelegate {
    suspend fun onRename(newName: String)
}

@Composable
fun rememberRenameState(router: Router, delegate: RenameDelegate): RenameState {
    val scope = rememberStateScope()
    val resources = rememberResources()
    return remember { RenameState(scope, router, delegate, resources) }
}

class RenameState(
    scope: CoroutineScope,
    private val router: Router,
    private val renameDelegate: RenameDelegate,
    private val resources: Res,
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
                error = resources.getString(R.string.outln_error_check_name)
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
    @StringRes dialogTitle: Int,
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
            action = R.string.outln_action_save to {
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
            label = { Text(stringResource(R.string.outln_label_name)) },
            placeholder = { Text(defaultName) },
            isError = state.error.isNotEmpty(),
            supportingText = { Text(state.error) },
            keyboardOptions = KeyboardOptions(KeyboardCapitalization.Words),
            maxLines = 4,
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}
