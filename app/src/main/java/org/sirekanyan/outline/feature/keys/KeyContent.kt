package org.sirekanyan.outline.feature.keys

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.api.model.getHost
import org.sirekanyan.outline.text.formatTraffic

@Composable
fun KeyContent(key: Key, withServer: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .heightIn(min = if (withServer) 72.dp else 56.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        Arrangement.SpaceBetween,
        Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f), Arrangement.Center) {
            Text(
                text = key.accessKey.nameOrDefault,
                overflow = Ellipsis,
                maxLines = 1,
            )
            if (withServer) {
                Text(
                    key.server.name.ifEmpty { key.server.getHost() },
                    overflow = Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        key.traffic?.let { traffic ->
            Text(
                text = formatTraffic(traffic),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}
