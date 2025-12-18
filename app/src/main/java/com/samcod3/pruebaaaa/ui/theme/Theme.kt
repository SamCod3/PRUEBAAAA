package com.samcod3.pruebaaaa.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlue,
    onPrimary = Color.White,
    primaryContainer = SkyBlueDark,
    onPrimaryContainer = Color.White,
    secondary = SunYellow,
    onSecondary = Color.Black,
    secondaryContainer = AccentOrange,
    onSecondaryContainer = Color.White,
    tertiary = AccentGreen,
    onTertiary = Color.White,
    background = BackgroundDark,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
    surfaceVariant = CardDark,
    onSurfaceVariant = Color(0xFFB0BEC5),
    error = AccentRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = SkyBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3F2FD),
    onPrimaryContainer = SkyBlueDark,
    secondary = SunYellow,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFF8E1),
    onSecondaryContainer = Color(0xFF5D4037),
    tertiary = AccentGreen,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = Color(0xFF1C1B1F),
    surface = SurfaceLight,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = CardLight,
    onSurfaceVariant = Color(0xFF49454F),
    error = AccentRed,
    onError = Color.White
)

@Composable
fun PRUEBAAAATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Cambiado a false para usar nuestra paleta
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
            // Usar un color más sutil para la status bar
            window.statusBarColor = if (darkTheme) {
                BackgroundDark.toArgb()
            } else {
                SkyBlue.toArgb()
            }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WeatherTypography,
        content = content
    )
}
