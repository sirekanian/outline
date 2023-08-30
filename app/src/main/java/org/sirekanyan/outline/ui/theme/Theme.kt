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

@Suppress("unused")
private val seed = Color(0xFF59EECE)

private val darkColors = darkColorScheme(
    primary = Color(0xFF43DDBE),
    onPrimary = Color(0xFF00382E),
    primaryContainer = Color(0xFF005143),
    onPrimaryContainer = Color(0xFF67FAD9),
    secondary = Color(0xFFB1CCC3),
    onSecondary = Color(0xFF1D352F),
    secondaryContainer = Color(0xFF334B45),
    onSecondaryContainer = Color(0xFFCDE8DF),
    tertiary = Color(0xFFAACBE3),
    onTertiary = Color(0xFF103447),
    tertiaryContainer = Color(0xFF2A4A5F),
    onTertiaryContainer = Color(0xFFC7E7FF),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF191C1B),
    onBackground = Color(0xFFE1E3E0),
    surface = Color(0xFF191C1B),
    onSurface = Color(0xFFE1E3E0),
    surfaceVariant = Color(0xFF3F4945),
    onSurfaceVariant = Color(0xFFBFC9C4),
    outline = Color(0xFF89938F),
    inverseOnSurface = Color(0xFF191C1B),
    inverseSurface = Color(0xFFE1E3E0),
    inversePrimary = Color(0xFF006B5A),
    surfaceTint = Color(0xFF43DDBE),
    outlineVariant = Color(0xFF3F4945),
    scrim = Color(0xFF000000),
)

private val lightColors = lightColorScheme(
    primary = Color(0xFF006B5A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF67FAD9),
    onPrimaryContainer = Color(0xFF00201A),
    secondary = Color(0xFF4B635C),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCDE8DF),
    onSecondaryContainer = Color(0xFF07201A),
    tertiary = Color(0xFF426277),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFC7E7FF),
    onTertiaryContainer = Color(0xFF001E2E),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFAFDFA),
    onBackground = Color(0xFF191C1B),
    surface = Color(0xFFFAFDFA),
    onSurface = Color(0xFF191C1B),
    surfaceVariant = Color(0xFFDBE5E0),
    onSurfaceVariant = Color(0xFF3F4945),
    outline = Color(0xFF6F7975),
    inverseOnSurface = Color(0xFFEFF1EF),
    inverseSurface = Color(0xFF2E3130),
    inversePrimary = Color(0xFF43DDBE),
    surfaceTint = Color(0xFF006B5A),
    outlineVariant = Color(0xFFBFC9C4),
    scrim = Color(0xFF000000),
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
