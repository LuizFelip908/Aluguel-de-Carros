package com.example.aluguelcarros.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = BluePrimary,
    secondary = BlueSecondary,
    tertiary = BlueTertiary,
    surface = BlueSurface,
)

private val DarkColors = darkColorScheme(
    primary = BluePrimaryLight,
    secondary = ColorTokens.DarkSecondary,
    tertiary = ColorTokens.DarkTertiary,
)

private object ColorTokens {
    val DarkSecondary = androidx.compose.ui.graphics.Color(0xFFB5C7FF)
    val DarkTertiary = androidx.compose.ui.graphics.Color(0xFFD0BCFF)
}

@Composable
fun AluguelCarrosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
