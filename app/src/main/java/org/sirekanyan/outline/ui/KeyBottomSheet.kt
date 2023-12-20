package org.sirekanyan.outline.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.launch
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.ext.showToast
import org.sirekanyan.outline.ui.icons.IconCopy

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun KeyBottomSheet(
    key: Key,
    onDismissRequest: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val localClipboard = LocalClipboardManager.current
    val localContext = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    SimpleBottomSheet(
        title = key.nameOrDefault,
        onDismissRequest = onDismissRequest,
        items = { sheetState ->
            ListItem(
                headlineContent = { Text("Copy") },
                leadingContent = { Icon(IconCopy, null) },
                modifier = Modifier.clickable {
                    localClipboard.setText(AnnotatedString(key.accessUrl))
                    localContext.showToast("Copied")
                    coroutineScope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        onDismissRequest()
                    }
                },
            )
            ListItem(
                headlineContent = { Text("Share") },
                leadingContent = { Icon(Icons.Default.Share, null) },
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        onDismissRequest()
                    }
                    localContext.startActivity(
                        Intent(Intent.ACTION_SEND)
                            .putExtra(Intent.EXTRA_TEXT, key.accessUrl)
                            .setType("text/plain")
                    )
                },
            )
            ListItem(
                headlineContent = { Text("Edit") },
                leadingContent = { Icon(Icons.Default.Edit, null) },
                modifier = Modifier.clickable {
                    onEditClick()
                    onDismissRequest()
                },
            )
            ListItem(
                headlineContent = { Text("Delete") },
                leadingContent = { Icon(Icons.Default.Delete, null) },
                modifier = Modifier.clickable {
                    onDeleteClick()
                    coroutineScope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        onDismissRequest()
                    }
                },
            )
        },
    )
}
