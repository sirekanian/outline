package org.sirekanyan.outline.ui

import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import org.sirekanyan.outline.api.model.AccessKey

@Composable
fun KeyContent(key: AccessKey) {
    val localContext = LocalContext.current
    val localClipboard = LocalClipboardManager.current
    Text(
        text = key.name.ifEmpty { "Key ${key.id}" },
        modifier = Modifier
            .clickable {
                localClipboard.setText(AnnotatedString(key.accessUrl))
                Toast.makeText(localContext, "Copied", LENGTH_SHORT).show()
            }
            .fillMaxWidth()
            .padding(16.dp),
    )
}