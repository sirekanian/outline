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
import org.sirekanyan.outline.HelloPage
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.ext.plus
import org.sirekanyan.outline.feature.sort.Sorting

@Composable
fun KeysContent(insets: PaddingValues, state: MainState, keys: List<Key>, sorting: Sorting) {
    val sortedKeys by produceState(listOf(), keys, sorting.key) {
        value = withContext(Dispatchers.IO) {
            keys.sortedWith(sorting.comparator)
        }
    }
    LazyColumn(contentPadding = insets + PaddingValues(top = 4.dp, bottom = 4.dp)) {
        sortedKeys.forEach { key ->
            item {
                val isDeleting = key.accessKey.accessUrl == state.deletingKey?.accessKey?.accessUrl
                val alpha = if (isDeleting) 0.5f else 1f
                CompositionLocalProvider(
                    LocalContentColor provides LocalContentColor.current.copy(alpha = alpha)
                ) {
                    KeyContent(key, state.page is HelloPage, onClick = { state.selectedKey = key })
                }
            }
        }
    }
}
