package org.sirekanyan.outline.feature.keys

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.sirekanyan.outline.R

@Composable
fun KeysErrorContent(insets: PaddingValues, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(insets).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.outln_network_error),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Button(onClick = onRetry) {
            Text(text = "Try again")
        }
    }
}
