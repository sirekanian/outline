package org.sirekanyan.outline.ui.theme

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.S
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val darkColors = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    secondary = Color(0xFFCCC2DC),
    tertiary = Color(0xFFEFB8C8),
)

private val lightColors = lightColorScheme(
    primary = Color(0xFF6650a4),
    secondary = Color(0xFF625b71),
    tertiary = Color(0xFF7D5260),
)

@Composable
fun OutlineTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val colors = when {
        isDark && SDK_INT >= S -> dynamicDarkColorScheme(context)
        SDK_INT >= S -> dynamicLightColorScheme(context)
        isDark -> darkColors
        else -> lightColors
    }
    MaterialTheme(colors, content = content)
}
