package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TvColorScheme = darkColorScheme(
    primary = GoogleBlue,
    onPrimary = TvTextPrimary,
    primaryContainer = TvSurfaceElevated,
    onPrimaryContainer = TvTextPrimary,
    secondary = GoogleGreen,
    onSecondary = TvTextPrimary,
    secondaryContainer = TvSurfaceVariant,
    onSecondaryContainer = TvTextPrimary,
    tertiary = GoogleYellow,
    onTertiary = TvBackground,
    background = TvBackground,
    onBackground = TvTextPrimary,
    surface = TvSurface,
    onSurface = TvTextPrimary,
    surfaceVariant = TvSurfaceVariant,
    onSurfaceVariant = TvTextSecondary,
    outline = TvBorderFocus
)

@Composable
fun TvGoogleMapTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TvColorScheme,
        typography = Typography,
        content = content
    )
}
