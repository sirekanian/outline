package org.sirekanyan.outline

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberResources(): Res {
    val context = LocalContext.current
    return remember { context.app().res }
}

interface Res {
    fun getString(@StringRes resId: Int): String
}

class ResImpl(private val app: App) : Res {
    override fun getString(resId: Int): String =
        app.getString(resId)
}
