package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = KitchenGreenDark,
    secondary = KitchenGreenSecondaryDark,
    tertiary = KitchenOrangeDark,
    background = KitchenDarkBackground,
    surface = KitchenDarkSurface,
    surfaceVariant = KitchenDarkSurfaceVariant,
    onPrimary = Color(0xFF051E10),
    onSecondary = Color(0xFF1B2620),
    onTertiary = Color(0xFF2C1005),
    onBackground = Color(0xFFE1EBF5),
    onSurface = Color(0xFFE1EBF5),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = KitchenGreen,
    secondary = KitchenGreenSecondary,
    tertiary = KitchenOrangeAccent,
    background = KitchenLightBackground,
    surface = KitchenLightSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF0F1E15),
    onSurface = Color(0xFF0F1E15),
  )

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
