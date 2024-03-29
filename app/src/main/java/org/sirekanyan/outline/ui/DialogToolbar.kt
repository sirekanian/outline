package org.sirekanyan.outline.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DialogToolbar(
    @StringRes title: Int,
    onCloseClick: () -> Unit,
    action: Pair<Int, () -> Unit>,
    isLoading: Boolean,
) {
    TopAppBar(
        title = { Text(stringResource(title), maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            IconButton({ onCloseClick() }) { Icon(Icons.Default.Close, null) }
        },
        actions = {
            if (isLoading) {
                CircularProgressIndicator(Modifier.size(56.dp).padding(16.dp), strokeWidth = 2.dp)
            } else {
                val (actionName, onActionClick) = action
                TextButton({ onActionClick() }) { Text(stringResource(actionName)) }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
        ),
    )
}
