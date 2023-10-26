package org.sirekanyan.outline.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import org.sirekanyan.outline.api.model.Key

@Composable
fun DeleteKeyContent(key: Key, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    ConfirmationAlertDialog(
        text = "Are you sure you want to delete the key named \"${key.accessKey.nameOrDefault}\"?",
        onDismiss = onDismiss,
        onConfirm = onConfirm,
    )
}

@Composable
fun DeleteServerContent(serverName: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    ConfirmationAlertDialog(
        text = "Are you sure you want to delete the server named \"$serverName\"?",
        onDismiss = onDismiss,
        onConfirm = onConfirm,
    )
}

@Composable
private fun ConfirmationAlertDialog(text: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        icon = { Icon(Icons.Default.Delete, null) },
        title = { Text("Confirmation") },
        text = { Text(text) },
        onDismissRequest = onDismiss,
        dismissButton = { TextButton(onDismiss) { Text("Cancel") } },
        confirmButton = {
            TextButton(onClick = { onConfirm(); onDismiss() }) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
    )
}
