package com.uns.backtracker.vista

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Paleta de colores llamativa y vibrante (estilo Dark Modern / Neon)
val BgColor = Color(0xFF0F111A) // Very Dark Blue/Black
val VisitadaColor = Color(0xFF1E2136) // Dark grey-blue for visited
val ParedColor = Color(0xFF00E5FF) // Neon Cyan
val MineroColor = Color(0xFFFFE600) // Neon Yellow
val CaminoColor = Color(0xFF00FF66) // Neon Green
val CuartoCentroColor = Color(0xFFFF007F) // Hot Pink
val InicioColor = Color(0xFFB300FF) // Bright Purple
val RutaOptimaColor = Color(0xFFFF9900) // Neon Orange para la ruta óptima

private val DarkColors = darkColorScheme(
    primary = Color(0xFF00E5FF),      // Neon Cyan
    secondary = Color(0xFFFF007F),    // Hot Pink
    tertiary = Color(0xFF00FF66),     // Neon Green
    background = BgColor,
    surface = Color(0xFF1A1D2D),      // Slightly lighter surface
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    errorContainer = Color(0xFFFF3B30),
    onErrorContainer = Color.White
)

// Tipografía personalizada para darle un toque más moderno
private val AppTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        letterSpacing = 1.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = 0.5.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.25.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp
    )
)

@Composable
fun MazeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = AppTypography,
        content = content
    )
}
