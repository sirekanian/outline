package org.sirekanyan.outline.ui

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
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.text.formatTraffic

@Composable
fun KeyContent(key: Key, onClick: () -> Unit) {
    Row(
        Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(16.dp),
        Arrangement.SpaceBetween,
        Alignment.CenterVertically,
    ) {
        Text(key.accessKey.nameOrDefault, Modifier.weight(1f), overflow = Ellipsis, maxLines = 1)
        key.traffic?.let { traffic ->
            Text(
                text = formatTraffic(traffic),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}
