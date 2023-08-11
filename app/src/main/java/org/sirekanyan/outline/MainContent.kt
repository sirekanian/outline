package org.sirekanyan.outline

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import org.sirekanyan.outline.api.model.AccessKey
import org.sirekanyan.outline.ui.KeyContent

@Composable
fun MainContent(keys: List<AccessKey>) {
    LazyColumn {
        keys.forEach { key ->
            item {
                KeyContent(key)
            }
        }
    }
}