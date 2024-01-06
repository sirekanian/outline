package org.sirekanyan.outline

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.sirekanyan.outline.ext.installOutline
import org.sirekanyan.outline.ext.isOutlineInstalled
import org.sirekanyan.outline.ext.openOutline

@Composable
fun NotSupportedContent(onDismissRequest: () -> Unit) {
    val context = LocalContext.current
    val isInstalled = remember { isOutlineInstalled(context) }
    AlertDialog(
        icon = { Icon(Icons.Default.Info, null) },
        title = { Text(stringResource(R.string.outln_title_not_supported)) },
        text = {
            Text(
                text = stringResource(
                    if (isInstalled) {
                        R.string.outln_text_not_supported_open
                    } else {
                        R.string.outln_text_not_supported_install
                    }
                ),
            )
        },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.outln_btn_cancel))
            }
        },
        confirmButton = {
            if (isInstalled) {
                TextButton(onClick = { onDismissRequest(); openOutline(context) }) {
                    Text(stringResource(R.string.outln_btn_not_supported_open))
                }
            } else {
                TextButton(onClick = { onDismissRequest(); installOutline(context) }) {
                    Text(stringResource(R.string.outln_btn_not_supported_install))
                }
            }
        },
    )
}
