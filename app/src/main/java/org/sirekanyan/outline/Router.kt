package org.sirekanyan.outline

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.sirekanyan.outline.ext.rememberStateScope

@Composable
fun rememberRouter(): Router {
    val scope = rememberStateScope()
    val page = rememberSaveable { mutableStateOf<Page>(HelloPage) }
    val dialog = rememberSaveable { mutableStateOf<Dialog?>(null) }
    return remember { Router(scope, page, dialog) }
}

class Router(
    private val scope: CoroutineScope,
    val pageState: MutableState<Page>,
    val dialogState: MutableState<Dialog?>,
) {

    val drawer = DrawerState(DrawerValue.Closed)
    var page by pageState
    var dialog by dialogState

    fun openDrawer() {
        scope.launch {
            drawer.open()
        }
    }

    fun closeDrawer(animated: Boolean = true) {
        scope.launch {
            if (animated) {
                drawer.close()
            } else {
                drawer.snapTo(DrawerValue.Closed)
            }
        }
    }

}
