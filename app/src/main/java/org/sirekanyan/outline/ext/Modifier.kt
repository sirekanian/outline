package org.sirekanyan.outline.ext

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.pointerInputOnDown(key: Any?, onDownEvent: () -> Unit): Modifier =
    pointerInput(key) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false)
            onDownEvent()
        }
    }
