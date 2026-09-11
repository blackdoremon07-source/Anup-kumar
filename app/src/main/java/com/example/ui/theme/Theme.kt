package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = DgBlueAccent,
    onPrimary = Color.White,
    secondary = DgAmber,
    onSecondary = Color.Black,
    tertiary = DgEmerald,
    background = DgBackgroundDark,
    surface = DgSurfaceDark,
    onBackground = DgTextPrimaryDark,
    onSurface = DgTextPrimaryDark,
    outline = DgBorderDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = DgNavyPrimary,
    onPrimary = Color.White,
    secondary = DgSaffron,
    onSecondary = Color.White,
    tertiary = DgEmerald,
    onTertiary = Color.White,
    background = DgBackgroundLight,
    surface = DgSurfaceLight,
    onBackground = DgTextPrimary,
    onSurface = DgTextPrimary,
    surfaceVariant = DgBlueLight,
    onSurfaceVariant = DgNavyPrimary,
    outline = DgBorderLight
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun DgTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  MyApplicationTheme(darkTheme = darkTheme, dynamicColor = false, content = content)
}

