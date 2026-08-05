package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = SurfaceWhite,
    primaryContainer = TealContainer,
    onPrimaryContainer = OnTealContainer,
    secondary = NavyDark,
    onSecondary = SurfaceWhite,
    secondaryContainer = SurfaceVariantLight,
    onSecondaryContainer = TextPrimary,
    tertiary = SafeGreen,
    onTertiary = SurfaceWhite,
    tertiaryContainer = SafeGreenBg,
    onTertiaryContainer = BadgeVerifiedText,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = BorderStrong,
    error = DangerRed,
    onError = SurfaceWhite,
    errorContainer = DangerRedBg,
    onErrorContainer = DangerRed
)

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimaryLight,
    onPrimary = NavyDark,
    primaryContainer = OnTealContainer,
    onPrimaryContainer = TealContainer,
    secondary = TealPrimaryLight,
    onSecondary = NavyDark,
    secondaryContainer = NavySurface,
    onSecondaryContainer = BackgroundLight,
    tertiary = SafeGreen,
    onTertiary = NavyDark,
    background = NavyDark,
    onBackground = BackgroundLight,
    surface = NavySurface,
    onSurface = BackgroundLight,
    surfaceVariant = NavyDark,
    onSurfaceVariant = BorderSubtle,
    outline = BorderSubtle,
    outlineVariant = BorderStrong,
    error = DangerRed,
    onError = SurfaceWhite
)

@Composable
fun SafeNestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent SafeNest brand colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Keep alias for compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    SafeNestTheme(darkTheme = darkTheme, content = content)
}
