package org.sirekanyan.outline.feature.keys

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.ext.plus
import org.sirekanyan.outline.feature.sort.Sorting

@Composable
fun KeysContent(insets: PaddingValues, state: MainState, keys: KeysSuccessState, sorting: Sorting) {
    val sortedKeys by produceState(listOf(), keys.values, sorting.key) {
        value = withContext(Dispatchers.IO) {
            keys.values.sortedWith(sorting.comparator)
        }
    }
    LazyColumn(contentPadding = insets + PaddingValues(bottom = 88.dp)) {
        sortedKeys.forEach { key ->
            item {
                val isDeleting = key.accessKey.accessUrl == state.deletingKey?.accessKey?.accessUrl
                val alpha = if (isDeleting) 0.5f else 1f
                CompositionLocalProvider(
                    LocalContentColor provides LocalContentColor.current.copy(alpha = alpha)
                ) {
                    KeyContent(key, onClick = { state.selectedKey = key })
                }
            }
        }
    }
}
