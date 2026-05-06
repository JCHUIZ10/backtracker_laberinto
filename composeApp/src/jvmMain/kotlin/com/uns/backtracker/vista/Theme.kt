package com.uns.backtracker.vista

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

// Paleta de colores Neón / Cyberpunk
val BgColor = Color(0xFF0D0D0D)
val ParedColor = Color(0xFF303030)
val VisitadaColor = Color(0xFF1A1A1A)
val MineroColor = Color(0xFF00FFCC)
val RutaOptimaColor = Color(0xFFCCFF00)
val RutaBotColor = Color(0xFFFF6600)
val InicioColor = Color(0xFF9900FF)
val CuartoCentroColor = Color(0xFFFF0066)

private val DarkColorScheme = darkColorScheme(
    primary = MineroColor,
    secondary = RutaOptimaColor,
    tertiary = InicioColor,
    surface = BgColor,
    onSurface = Color.White
)

private val MazeTypography = Typography(
    titleLarge = androidx.compose.ui.text.TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = androidx.compose.ui.text.TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp
    )
)

private val MazeShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp)
)

@Composable
fun MazeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = MazeTypography,
        shapes = MazeShapes,
        content = content
    )
}
