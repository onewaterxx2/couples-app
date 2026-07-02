package com.example.myp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 固定的品牌浅色配色 —— 不跟随系统壁纸变色，保证观感统一可控。
private val AppColorScheme = lightColorScheme(
    primary = CloverGreen,
    onPrimary = OnDark,
    primaryContainer = CloverGreenSoft,
    onPrimaryContainer = CloverGreenDark,

    secondary = Rose,
    onSecondary = OnDark,
    secondaryContainer = RoseSoft,
    onSecondaryContainer = TextPrimary,

    tertiary = Honey,
    onTertiary = CloverInk,
    tertiaryContainer = WarmSurfaceVariant,
    onTertiaryContainer = CloverInk,

    background = WarmPaper,
    onBackground = TextPrimary,

    surface = WarmSurface,
    onSurface = TextPrimary,
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    outline = Outline,
    outlineVariant = OutlineVariant,

    error = ErrorColor,
    onError = OnDark,
    errorContainer = ErrorContainer,
    onErrorContainer = ErrorColor
)

@Composable
fun MypTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // 固定浅色基调，不启用动态取色
    val colorScheme = AppColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
