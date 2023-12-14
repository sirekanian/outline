package org.sirekanyan.outline.db

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.sirekanyan.outline.app

@Composable
fun rememberDebugDao(): DebugDao {
    val database = LocalContext.current.app().database
    return remember { DebugDaoImpl(database) }
}

interface DebugDao {
    fun reset()
}