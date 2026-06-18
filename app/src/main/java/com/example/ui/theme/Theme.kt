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

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00D2FF), // Glowing Electric Cyan
    onPrimary = Color(0xFF030A16),
    primaryContainer = Color(0xFF1E293B), // Sleek container
    onPrimaryContainer = Color(0xFFE2E8F0),
    secondary = Color(0xFF10B981), // Emerald accent for active speeds/statuses
    background = Color(0xFF0A0F1D), // Elegant deep void
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF111A2E), // Polished elevated sheets
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF1E294B), // Gorgeous variant card container
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155) // Clean border contrast
)

private val LightColorScheme = lightColorScheme(
    primary = ProfessionalPrimary,
    onPrimary = ProfessionalOnPrimary,
    primaryContainer = ProfessionalPrimaryContainer,
    onPrimaryContainer = ProfessionalOnPrimaryContainer,
    background = ProfessionalBackground,
    onBackground = ProfessionalOnBackground,
    surface = ProfessionalSurface,
    onSurface = ProfessionalOnSurface,
    surfaceVariant = ProfessionalSurfaceVariant,
    outline = ProfessionalOutline
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
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
