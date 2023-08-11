package org.sirekanyan.outline

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MainState {
    val drawer = DrawerState(DrawerValue.Closed)
    var selected by mutableStateOf<Int?>(null)
}