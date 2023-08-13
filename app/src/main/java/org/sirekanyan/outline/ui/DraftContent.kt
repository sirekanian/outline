package org.sirekanyan.outline.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
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
import org.sirekanyan.outline.HelloPage
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.SelectedPage
import org.sirekanyan.outline.api.OutlineApi
import org.sirekanyan.outline.db.ApiUrlDao

@Composable
fun DraftContent(api: OutlineApi, dao: ApiUrlDao, state: MainState) {
    var draft by remember { mutableStateOf("") }
    var error by remember(draft) { mutableStateOf("") }
    suspend fun onAddClick() {
        try {
            api.getServerName(draft)
            dao.insertUrl(draft)
            state.page = SelectedPage(draft)
            state.closeDrawer(animated = false)
        } catch (exception: Exception) {
            exception.printStackTrace()
            error = "Check URL or try again"
        }
    }
    Column {
        DraftTopAppBar(
            onCloseClick = { state.page = HelloPage },
            onAddClick = { state.scope.launch { onAddClick() } },
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DraftTopAppBar(onCloseClick: () -> Unit, onAddClick: () -> Unit) {
    TopAppBar(
        title = { Text("Add server") },
        navigationIcon = {
            IconButton({ onCloseClick() }) { Icon(Icons.Default.Close, null) }
        },
        actions = {
            TextButton({ onAddClick() }) { Text("Add") }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
        ),
    )
}
