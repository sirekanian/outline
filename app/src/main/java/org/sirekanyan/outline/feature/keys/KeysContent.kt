package org.sirekanyan.outline.feature.keys

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.ext.plus

@Composable
fun KeysContent(insets: PaddingValues, state: MainState, keys: KeysSuccessState) {
    LazyColumn(contentPadding = insets + PaddingValues(bottom = 88.dp)) {
        keys.values.sortedByDescending(Key::traffic).forEach { key ->
            item {
                KeyContent(key, onClick = { state.selectedKey = key })
            }
        }
    }
}
