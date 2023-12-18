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
import org.sirekanyan.outline.ext.isOutlineInstalled
import org.sirekanyan.outline.ext.openGooglePlay
import org.sirekanyan.outline.ext.openOutline

@Composable
fun NotSupportedContent(onDismissRequest: () -> Unit) {
    val context = LocalContext.current
    val isInstalled = remember { isOutlineInstalled(context) }
    AlertDialog(
        icon = { Icon(Icons.Default.Info, null) },
        title = { Text("Not supported") },
        text = {
            Text(
                text = stringResource(R.string.outln_app_name) + " does not support ss:// links. " +
                        "Would you like to ${if (isInstalled) "open" else "install"} Outline?",
            )
        },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        confirmButton = {
            if (isInstalled) {
                TextButton(onClick = { onDismissRequest(); openOutline(context) }) {
                    Text("Open Outline")
                }
            } else {
                TextButton(onClick = { onDismissRequest(); openGooglePlay(context) }) {
                    Text("Install Outline")
                }
            }
        },
    )
}
