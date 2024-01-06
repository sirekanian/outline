package org.sirekanyan.outline.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.sirekanyan.outline.R
import org.sirekanyan.outline.api.model.Key

@Composable
fun DeleteKeyContent(key: Key, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    ConfirmationAlertDialog(
        text = stringResource(R.string.outln_text_confirm_key, key.nameOrDefault),
        onDismiss = onDismiss,
        onConfirm = onConfirm,
    )
}

@Composable
fun DeleteServerContent(serverName: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    ConfirmationAlertDialog(
        text = stringResource(R.string.outln_text_confirm_server, serverName),
        onDismiss = onDismiss,
        onConfirm = onConfirm,
    )
}

@Composable
private fun ConfirmationAlertDialog(text: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        icon = { Icon(Icons.Default.Delete, null) },
        title = { Text(stringResource(R.string.outln_title_confirm)) },
        text = { Text(text) },
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.outln_btn_cancel),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(); onDismiss() }) {
                Text(
                    text = stringResource(R.string.outln_btn_confirm_delete),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
    )
}
