package org.sirekanyan.outline.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

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
