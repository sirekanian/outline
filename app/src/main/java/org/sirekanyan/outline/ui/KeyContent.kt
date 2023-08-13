package org.sirekanyan.outline.ui

import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.text.formatTraffic

@Composable
fun KeyContent(key: Key) {
    val localContext = LocalContext.current
    val localClipboard = LocalClipboardManager.current
    Row(
        Modifier
            .clickable {
                localClipboard.setText(AnnotatedString(key.accessKey.accessUrl))
                Toast.makeText(localContext, "Copied", LENGTH_SHORT).show()
            }
            .fillMaxWidth()
            .padding(16.dp),
        Arrangement.SpaceBetween,
        Alignment.CenterVertically,
    ) {
        Text(key.accessKey.name.ifEmpty { "Key ${key.accessKey.id}" })
        key.traffic?.let { traffic ->
            Text(
                text = formatTraffic(traffic),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}
