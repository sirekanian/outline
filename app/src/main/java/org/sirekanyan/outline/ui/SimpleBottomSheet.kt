package org.sirekanyan.outline.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SimpleBottomSheet(
    title: String,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    items: @Composable () -> Unit,
) {
    // insets should be kept outside of the modal bottom sheet to work properly
    val insets = WindowInsets.navigationBars.asPaddingValues()
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RectangleShape,
        dragHandle = {},
        windowInsets = WindowInsets(0.dp),
    ) {
        Column(Modifier.padding(insets).padding(top = 4.dp)) {
            Text(title, Modifier.padding(16.dp), style = MaterialTheme.typography.labelLarge)
            items()
        }
    }
}
