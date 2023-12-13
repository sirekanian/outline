package org.sirekanyan.outline.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> rememberFlowAsState(initial: T, key: Any? = null, block: () -> Flow<T>): State<T> =
    remember(key, calculation = block).collectAsState(initial)
