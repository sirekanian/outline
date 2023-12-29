package org.sirekanyan.outline.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.sirekanyan.outline.BuildConfig
import org.sirekanyan.outline.R
import org.sirekanyan.outline.ui.icons.IconOpenInNew
import org.sirekanyan.outline.ui.icons.IconPlayStore

@Composable
fun AboutDialogContent(onDismiss: () -> Unit) {
    AlertDialog(
        icon = { Icon(Icons.Default.Info, null) },
        title = { Text(stringResource(R.string.outln_app_name) + " " + BuildConfig.VERSION_NAME) },
        text = {
            Text(
                "An application for managing Outline VPN servers. You can find more information " +
                        "on https://getoutline.org.",
            )
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            val context = LocalContext.current
            val playUri = "https://play.google.com/store/apps/details?id=${context.packageName}"
            TextButton(
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(playUri)))
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            ) {
                Icon(IconPlayStore, null, Modifier.padding(horizontal = 4.dp))
                Text(
                    text = "Rate on Play Store",
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(IconOpenInNew, null, Modifier.padding(horizontal = 8.dp))
            }
        },
    )
}
