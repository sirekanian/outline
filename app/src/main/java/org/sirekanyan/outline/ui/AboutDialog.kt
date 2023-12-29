package org.sirekanyan.outline.ui

import android.content.Intent
import android.content.Intent.ACTION_SENDTO
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.sirekanyan.outline.BuildConfig
import org.sirekanyan.outline.R
import org.sirekanyan.outline.ext.logDebug
import org.sirekanyan.outline.ext.showToast
import org.sirekanyan.outline.ui.icons.IconOpenInNew
import org.sirekanyan.outline.ui.icons.IconPlayStore

@Composable
fun AboutDialogContent(onDismiss: () -> Unit) {
    AlertDialog(
        icon = { Icon(Icons.Default.Info, null) },
        title = { Text(stringResource(R.string.outln_app_name) + " " + BuildConfig.VERSION_NAME) },
        text = {
            val context = LocalContext.current
            val annotatedString = buildAnnotatedString {
                append("An application for managing Outline VPN servers. ")
                append("You can find more information on ")
                withStyle(SpanStyle(MaterialTheme.colorScheme.primary)) {
                    pushStringAnnotation("link", "https://getoutline.org")
                    append("getoutline.org")
                    pop()
                }
            }
            val textColor = MaterialTheme.colorScheme.onSurfaceVariant
            val textStyle = MaterialTheme.typography.bodyMedium.copy(textColor)
            ClickableText(annotatedString, style = textStyle) { offset ->
                val links = annotatedString.getStringAnnotations("link", offset, offset)
                links.firstOrNull()?.item?.let { link ->
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
                    onDismiss()
                }
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            val context = LocalContext.current
            val clipboard = LocalClipboardManager.current
            AboutItem(Icons.Default.Email, "Send feedback") {
                val email = "outline@sirekanyan.org"
                val intent = Intent(ACTION_SENDTO, Uri.parse("mailto:$email"))
                try {
                    context.startActivity(intent)
                } catch (exception: Exception) {
                    logDebug("Cannot find email app", exception)
                    clipboard.setText(AnnotatedString(email))
                    context.showToast("Email is copied")
                }
            }
            val playUri = "https://play.google.com/store/apps/details?id=${context.packageName}"
            AboutItem(IconPlayStore, "Rate on Play Store") {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(playUri)))
                onDismiss()
            }
        },
    )
}

@Composable
private fun AboutItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
    ) {
        Icon(icon, null, Modifier.padding(horizontal = 4.dp))
        Text(
            text = text,
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Icon(IconOpenInNew, null, Modifier.padding(horizontal = 8.dp))
    }
}
