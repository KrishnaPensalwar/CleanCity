package com.example.cleancityapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = LightBackgroundPrimary,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = PrimaryLight,
    secondary = AccentTeal,
    onSecondary = LightBackgroundPrimary,
    secondaryContainer = DarkBackgroundSecondary,
    onSecondaryContainer = DarkTextPrimary,
    tertiary = PrimaryLight,
    background = DarkBackgroundTertiary,
    onBackground = DarkTextPrimary,
    surface = DarkBackgroundPrimary,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkBackgroundSecondary,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorderSecondary,
    outlineVariant = DarkBorderTertiary,
    error = BadgeDeclinedText,
    errorContainer = BadgeDeclinedBg,
    onErrorContainer = BadgeDeclinedText,
    tertiaryContainer = BadgeApprovedBg,
    onTertiaryContainer = BadgeApprovedText
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = LightBackgroundPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = PrimaryDark,
    secondary = AccentTeal,
    onSecondary = LightBackgroundPrimary,
    secondaryContainer = PrimaryLight,
    onSecondaryContainer = PrimaryDark,
    tertiary = PrimaryDark,
    background = LightBackgroundTertiary,
    onBackground = LightTextPrimary,
    surface = LightBackgroundPrimary,
    onSurface = LightTextPrimary,
    surfaceVariant = LightBackgroundSecondary,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorderSecondary,
    outlineVariant = LightBorderTertiary,
    error = BadgeDeclinedText,
    errorContainer = BadgeDeclinedBg,
    onErrorContainer = BadgeDeclinedText,
    tertiaryContainer = BadgeApprovedBg,
    onTertiaryContainer = BadgeApprovedText
)

@Composable
fun CleanCityAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
