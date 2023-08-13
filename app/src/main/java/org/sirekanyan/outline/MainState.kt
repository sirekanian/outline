package org.sirekanyan.outline

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberMainState(): MainState {
    val scope = rememberCoroutineScope()
    return remember { MainState(scope) }
}

class MainState(val scope: CoroutineScope) {

    val drawer = DrawerState(DrawerValue.Closed)
    var selected by mutableStateOf<String?>(null)

    fun closeDrawer() {
        scope.launch { drawer.close() }
    }

}