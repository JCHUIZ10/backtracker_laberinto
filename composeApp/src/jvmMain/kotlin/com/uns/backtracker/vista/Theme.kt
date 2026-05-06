package com.uns.backtracker.vista

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

// ─── Paleta "Neon Pure" ─────────────────────────────────────────────────
// Fondo negro profundo para hacer resaltar los neones
val BgColor           = Color(0xFF050505)   // Canvas principal
val BgPanelColor      = Color(0xFF0D0D0D)   // Panel lateral

// Muros del laberinto: Blanco puro para máximo contraste
val ParedColor        = Color(0xFFFFFFFF)

// Celda visitada durante generación: gris muy sutil
val VisitadaColor     = Color(0xFF1A1A1A)

// Minero / Explorador: Cian Neón brillante
val MineroColor       = Color(0xFF00FFFF)

// Ruta óptima: Verde Neón puro y vibrante
val RutaOptimaColor   = Color(0xFF39FF14)

// Ruta del Bot: Magenta/Rosa Neón (contraste radical con el verde)
val RutaBotColor      = Color(0xFFFF00FF)

// Celda de inicio: Púrpura eléctrico
val InicioColor       = Color(0xFFB026FF)

// Celda de destino: Rojo Neón
val CuartoCentroColor = Color(0xFFFF003C)

// Texto de escala de coordenadas: Blanco semi-transparente
val EscalaColor       = Color(0xFFAAAAAA)

// ─── Esquema de color Material 3 ────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary              = Color(0xFF39FF14), // Verde Neon
    onPrimary            = Color(0xFF000000),
    primaryContainer     = Color(0xFF114D06),
    onPrimaryContainer   = Color(0xFFB3FF99),

    secondary            = Color(0xFF00FFFF), // Cian Neon
    onSecondary          = Color(0xFF000000),
    secondaryContainer   = Color(0xFF004D4D),
    onSecondaryContainer = Color(0xFF99FFFF),

    tertiary             = Color(0xFFFF00FF), // Magenta Neon
    onTertiary           = Color(0xFF000000),

    error                = Color(0xFFFF003C),
    onError              = Color(0xFF000000),

    surface              = Color(0xFF0D0D0D),
    onSurface            = Color(0xFFFFFFFF),
    surfaceVariant       = Color(0xFF1A1A1A),
    onSurfaceVariant     = Color(0xFFCCCCCC),

    background           = Color(0xFF050505),
    onBackground         = Color(0xFFFFFFFF),

    outline              = Color(0xFF333333),
    outlineVariant       = Color(0xFF1A1A1A)
)

// ─── Tipografía ─────────────────────────────────────────────────────────────
private val MazeTypography = Typography(
    titleLarge = TextStyle(
        fontFamily   = FontFamily.Default,
        fontWeight   = FontWeight.Bold,
        fontSize     = 20.sp,
        letterSpacing = 0.3.sp
    ),
    titleMedium = TextStyle(
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 15.sp,
        letterSpacing = 0.2.sp
    ),
    titleSmall = TextStyle(
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 13.sp,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontSize     = 13.sp,
        letterSpacing = 0.1.sp
    ),
    bodySmall = TextStyle(
        fontSize     = 11.sp,
        letterSpacing = 0.1.sp
    ),
    labelLarge = TextStyle(
        fontWeight   = FontWeight.Medium,
        fontSize     = 13.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontWeight   = FontWeight.Medium,
        fontSize     = 11.sp,
        letterSpacing = 0.4.sp
    ),
    labelSmall = TextStyle(
        fontWeight   = FontWeight.Medium,
        fontSize     = 10.sp,
        letterSpacing = 0.4.sp
    )
)

private val MazeShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(6.dp),
    medium     = RoundedCornerShape(10.dp),
    large      = RoundedCornerShape(14.dp)
)

@Composable
fun MazeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = MazeTypography,
        shapes      = MazeShapes,
        content     = content
    )
}
