package org.sirekanyan.outline.ui

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.parcelize.Parcelize

@Composable
fun rememberSearchState(): SearchState =
    rememberSaveable(saver = SearchState.Saver) { SearchState() }

class SearchState(query: String = "", isOpened: Boolean = false) {

    var query by mutableStateOf(query)
    var isOpened by mutableStateOf(isOpened)

    fun openSearch() {
        isOpened = true
    }

    fun closeSearch() {
        query = ""
        isOpened = false
    }

    companion object {

        @Parcelize
        class Saveable(val query: String, val isOpened: Boolean) : Parcelable

        val Saver = Saver<SearchState, Saveable>(
            save = { Saveable(it.query, it.isOpened) },
            restore = { SearchState(it.query, it.isOpened) },
        )

    }

}
