package org.sirekanyan.outline.feature.keys

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.sirekanyan.outline.HelloPage
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.ext.plus
import org.sirekanyan.outline.ext.pointerInputOnDown
import org.sirekanyan.outline.feature.sort.Sorting

@Composable
fun KeysContent(insets: PaddingValues, state: MainState, keys: List<Key>, sorting: Sorting) {
    val sortedKeys by produceState(listOf(), keys, sorting.key) {
        value = withContext(Dispatchers.IO) {
            keys.sortedWith(sorting.comparator)
        }
    }
    val focusManager = LocalFocusManager.current
    LazyColumn(
        contentPadding = insets + PaddingValues(top = 4.dp, bottom = 4.dp),
        modifier = Modifier.fillMaxSize().pointerInputOnDown(Unit) { focusManager.clearFocus() },
    ) {
        sortedKeys.forEach { key ->
            item {
                val isDeleting = key.accessKey.accessUrl == state.deletingKey?.accessKey?.accessUrl
                KeyContent(
                    key = key,
                    withServer = state.page is HelloPage,
                    modifier = Modifier.alpha(if (isDeleting) 0.5f else 1f),
                    onClick = { state.selectedKey = key },
                )
            }
        }
    }
}
