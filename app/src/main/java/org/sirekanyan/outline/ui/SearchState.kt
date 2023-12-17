package org.sirekanyan.outline.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun rememberSearchState(): SearchState =
    remember { SearchState() }

class SearchState {

    var query by mutableStateOf("")
    var isOpened by mutableStateOf(false)

    fun openSearch() {
        isOpened = true
    }

    fun closeSearch() {
        query = ""
        isOpened = false
    }

}
