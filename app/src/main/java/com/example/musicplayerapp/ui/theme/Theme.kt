package com.example.musicplayerapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PlayerColors = darkColorScheme(
    primary = Color(0xFFB6F4CD), onPrimary = Color(0xFF10291D),
    primaryContainer = Color(0xFF253D32), onPrimaryContainer = Color(0xFFB6F4CD),
    secondaryContainer = Color(0xFF284738), onSecondaryContainer = Color(0xFFB6F4CD),
    secondary = Color(0xFFB5C7DE), background = Color(0xFF101319),
    surface = Color(0xFF101319), surfaceVariant = Color(0xFF242B33),
    onSurface = Color(0xFFF3F4F6), onSurfaceVariant = Color(0xFFB6BDC8),
    outline = Color(0xFF4A5360)
)

@Composable
fun MusicPlayerAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = PlayerColors, typography = Typography, content = content)
}
