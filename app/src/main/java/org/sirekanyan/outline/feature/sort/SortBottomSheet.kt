package org.sirekanyan.outline.feature.sort

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.sirekanyan.outline.ui.SimpleBottomSheet

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SortBottomSheet(
    sorting: Sorting,
    onSortingChange: (Sorting) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    SimpleBottomSheet(
        title = "Sort by…",
        onDismissRequest = onDismissRequest,
        items = { sheetState ->
            Sorting.entries.forEach { option ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(option.title),
                            color = if (sorting == option) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Unspecified
                            },
                        )
                    },
                    trailingContent = {
                        if (sorting == option) {
                            Icon(Icons.Default.Done, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    modifier = Modifier.clickable {
                        coroutineScope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            onDismissRequest()
                        }
                        onSortingChange(option)
                    },
                )
            }
        },
    )
}
