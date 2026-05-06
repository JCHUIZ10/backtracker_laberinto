package com.uns.backtracker.vista

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.uns.backtracker.dominio.eventos.EventoBot
import com.uns.backtracker.dominio.model.*
import com.uns.backtracker.viewmodel.MazeState

// Margen reservado para la escala de coordenadas
private const val ESCALA_PX = 24f

@OptIn(ExperimentalTextApi::class)
@Composable
fun MazeCanvas(estado: MazeState, onCellTap: (Int, Int) -> Unit = { _, _ -> }, modifier: Modifier = Modifier) {
    var escala by remember { mutableStateOf(1f) }
    var desplazamiento by remember { mutableStateOf(Offset.Zero) }
    var tamanoCeldaActual by remember { mutableStateOf(0f) }
    var margenXActual by remember { mutableStateOf(0f) }
    var margenYActual by remember { mutableStateOf(0f) }

    val botPainter = painterResource("bot.png")
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .background(BgColor)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    escala = (escala * zoom).coerceIn(0.1f, 10f)
                    desplazamiento += pan
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (tamanoCeldaActual > 0) {
                        val cxC = size.width / 2f
                        val cyC = size.height / 2f
                        val rx = (offset.x - cxC) / escala + cxC - desplazamiento.x / escala
                        val ry = (offset.y - cyC) / escala + cyC - desplazamiento.y / escala
                        val col = ((rx - margenXActual) / tamanoCeldaActual).toInt()
                        val fila = ((ry - margenYActual) / tamanoCeldaActual).toInt()
                        val lab = estado.animLaberinto
                        if (lab != null && fila in 0 until lab.configuracion.filas && col in 0 until lab.configuracion.columnas) {
                            onCellTap(fila, col)
                        }
                    }
                }
            }
    ) {
        val laberinto = estado.animLaberinto ?: return@Canvas
        val filas    = laberinto.configuracion.filas
        val columnas = laberinto.configuracion.columnas

        // Área neta descontando la escala
        val anchoNeto = (size.width  - ESCALA_PX) * 0.94f
        val altoNeto  = (size.height - ESCALA_PX) * 0.94f
        val tamanoCelda = minOf(anchoNeto / columnas, altoNeto / filas).coerceAtLeast(1f)

        val margenX = ESCALA_PX + ((size.width  - ESCALA_PX) - tamanoCelda * columnas) / 2f
        val margenY = ESCALA_PX + ((size.height - ESCALA_PX) - tamanoCelda * filas)  / 2f

        tamanoCeldaActual = tamanoCelda
        margenXActual = margenX
        margenYActual = margenY

        val cxCanvas = size.width / 2f
        val cyCanvas = size.height / 2f

        // Funciones para transformar coordenadas del laberinto a coordenadas del canvas con zoom
        fun tx(x: Float) = (x - cxCanvas) * escala + cxCanvas + desplazamiento.x
        fun ty(y: Float) = (y - cyCanvas) * escala + cyCanvas + desplazamiento.y

        // ── 0. Escala de coordenadas (sin zoom, siempre legible) ──────────
        dibujarEscala(filas, columnas, tamanoCelda, margenX, margenY, ::tx, ::ty, textMeasurer)

        // ── Aplicar transformación de zoom/pan para el contenido del laberinto ──
        drawContext.canvas.save()
        drawContext.canvas.translate(cxCanvas + desplazamiento.x, cyCanvas + desplazamiento.y)
        drawContext.canvas.scale(escala, escala)
        drawContext.canvas.translate(-cxCanvas, -cyCanvas)

        // ── 1. Celdas visitadas ───────────────────────────────────────────
        estado.visitadas.forEach { pos ->
            drawRect(
                color   = MineroColor.copy(alpha = 0.10f),
                topLeft = Offset(margenX + pos.columna * tamanoCelda, margenY + pos.fila * tamanoCelda),
                size    = Size(tamanoCelda, tamanoCelda)
            )
        }

        // ── 2. Paredes ────────────────────────────────────────────────────
        val grosorPared = (tamanoCelda * 0.10f).coerceIn(1f, 3.5f)
        for (r in 0 until filas) {
            for (c in 0 until columnas) {
                val celda = laberinto.grilla[r][c]
                val px = margenX + c * tamanoCelda
                val py = margenY + r * tamanoCelda
                if (celda.tienePared(Direccion.ARRIBA))
                    drawLine(ParedColor, Offset(px, py), Offset(px + tamanoCelda, py), grosorPared, cap = StrokeCap.Square)
                if (celda.tienePared(Direccion.IZQUIERDA))
                    drawLine(ParedColor, Offset(px, py), Offset(px, py + tamanoCelda), grosorPared, cap = StrokeCap.Square)
                if (c == columnas - 1 && celda.tienePared(Direccion.DERECHA))
                    drawLine(ParedColor, Offset(px + tamanoCelda, py), Offset(px + tamanoCelda, py + tamanoCelda), grosorPared, cap = StrokeCap.Square)
                if (r == filas - 1 && celda.tienePared(Direccion.ABAJO))
                    drawLine(ParedColor, Offset(px, py + tamanoCelda), Offset(px + tamanoCelda, py + tamanoCelda), grosorPared, cap = StrokeCap.Square)
            }
        }

        // ── 3. Inicio y Fin ───────────────────────────────────────────────
        val inicio = laberinto.configuracion.inicio
        val fin    = laberinto.configuracion.fin
        drawRect(InicioColor.copy(alpha = 0.50f),
            Offset(margenX + inicio.columna * tamanoCelda, margenY + inicio.fila * tamanoCelda), Size(tamanoCelda, tamanoCelda))
        drawRect(CuartoCentroColor.copy(alpha = 0.50f),
            Offset(margenX + fin.columna * tamanoCelda, margenY + fin.fila * tamanoCelda), Size(tamanoCelda, tamanoCelda))

        // ── 4. Ruta Óptima ────────────────────────────────────────────────
        if (estado.mostrarRutaOptima && estado.caminoOptimoVisual.isNotEmpty()) {
            val grosorRuta = tamanoCelda * 0.22f
            for (i in 0 until estado.caminoOptimoVisual.size - 1) {
                val a = estado.caminoOptimoVisual[i]
                val b = estado.caminoOptimoVisual[i + 1]
                drawLine(
                    color = RutaOptimaColor.copy(alpha = 0.85f),
                    start = Offset(margenX + a.columna * tamanoCelda + tamanoCelda / 2f, margenY + a.fila * tamanoCelda + tamanoCelda / 2f),
                    end   = Offset(margenX + b.columna * tamanoCelda + tamanoCelda / 2f, margenY + b.fila * tamanoCelda + tamanoCelda / 2f),
                    strokeWidth = grosorRuta, cap = StrokeCap.Round
                )
            }
        }

        // ── 4.5. Ruta del Bot ─────────────────────────────────────────────
        if (estado.mostrarRutaBot) {
            val rutaBot = obtenerRutaActualBot(estado)
            if (rutaBot.isNotEmpty()) {
                val grosorBot = tamanoCelda * 0.16f
                for (i in 0 until rutaBot.size - 1) {
                    val a = rutaBot[i]
                    val b = rutaBot[i + 1]
                    drawLine(
                        color = RutaBotColor.copy(alpha = 0.85f),
                        start = Offset(margenX + a.columna * tamanoCelda + tamanoCelda / 2f, margenY + a.fila * tamanoCelda + tamanoCelda / 2f),
                        end   = Offset(margenX + b.columna * tamanoCelda + tamanoCelda / 2f, margenY + b.fila * tamanoCelda + tamanoCelda / 2f),
                        strokeWidth = grosorBot, cap = StrokeCap.Round
                    )
                }
            }
        }

        // ── 5. Minero ─────────────────────────────────────────────────────
        estado.mineroPos?.let { pos ->
            val px = margenX + pos.columna * tamanoCelda
            val py = margenY + pos.fila * tamanoCelda
            val tam = tamanoCelda * 0.55f
            val mg  = (tamanoCelda - tam) / 2f
            drawOval(MineroColor.copy(alpha = 0.25f), Offset(px + mg - 2f, py + mg - 2f), Size(tam + 4f, tam + 4f))
            drawOval(MineroColor, Offset(px + mg, py + mg), Size(tam, tam))
        }

        // ── 6. Bot ────────────────────────────────────────────────────────
        estado.botPosActual?.let { pos ->
            val px    = margenX + pos.columna * tamanoCelda
            val py    = margenY + pos.fila * tamanoCelda
            val tamBot = tamanoCelda * 0.78f
            val mg    = (tamanoCelda - tamBot) / 2f
            drawOval(RutaBotColor.copy(alpha = 0.30f), Offset(px + mg - 3f, py + mg - 3f), Size(tamBot + 6f, tamBot + 6f))
            drawContext.canvas.save()
            drawContext.canvas.translate(px + mg, py + mg)
            with(botPainter) { draw(size = Size(tamBot, tamBot)) }
            drawContext.canvas.restore()
        }

        drawContext.canvas.restore()
    }
}

// ─── Escala de coordenadas tipo ajedrez ─────────────────────────────────────
@OptIn(ExperimentalTextApi::class)
private fun DrawScope.dibujarEscala(
    filas: Int, columnas: Int,
    tamanoCelda: Float,
    margenX: Float, margenY: Float,
    tx: (Float) -> Float,
    ty: (Float) -> Float,
    textMeasurer: TextMeasurer
) {
    val lineaColor = EscalaColor.copy(alpha = 0.30f)
    val fontSize = (ESCALA_PX * 0.58f).coerceIn(8f, 13f)
    val textStyle = TextStyle(
        color = EscalaColor,
        fontSize = fontSize.sp,
        fontWeight = FontWeight.Medium
    )

    // Números de columna (eje superior)
    for (c in 0 until columnas) {
        val cx = tx(margenX + c * tamanoCelda + tamanoCelda / 2f)
        if (cx < ESCALA_PX || cx > size.width) continue
        val label = c.toString()
        
        val textLayoutResult = textMeasurer.measure(label, textStyle)
        val tw = textLayoutResult.size.width
        val th = textLayoutResult.size.height
        
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            topLeft = Offset(cx - tw / 2f, (ESCALA_PX - th) / 2f + 2f),
            style = textStyle
        )
    }

    // Números de fila (eje izquierdo)
    for (r in 0 until filas) {
        val cy = ty(margenY + r * tamanoCelda + tamanoCelda / 2f)
        if (cy < ESCALA_PX || cy > size.height) continue
        val label = r.toString()
        
        val textLayoutResult = textMeasurer.measure(label, textStyle)
        val tw = textLayoutResult.size.width
        val th = textLayoutResult.size.height
        
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            topLeft = Offset((ESCALA_PX - tw) / 2f, cy - th / 2f),
            style = textStyle
        )
    }

    // Guías de tick sutiles
    for (c in 0 until columnas) {
        val cx = tx(margenX + c * tamanoCelda + tamanoCelda / 2f)
        if (cx < ESCALA_PX || cx > size.width) continue
        drawLine(lineaColor, Offset(cx, ESCALA_PX - 2f), Offset(cx, ESCALA_PX + 3f), 0.8f)
    }
    for (r in 0 until filas) {
        val cy = ty(margenY + r * tamanoCelda + tamanoCelda / 2f)
        if (cy < ESCALA_PX || cy > size.height) continue
        drawLine(lineaColor, Offset(ESCALA_PX - 2f, cy), Offset(ESCALA_PX + 3f, cy), 0.8f)
    }
}

// ─── Reconstrucción del camino activo del Bot ────────────────────────────────
private fun obtenerRutaActualBot(estado: MazeState): List<Coordenada> {
    val ruta = mutableListOf<Coordenada>()
    val eventos = estado.eventosBot.take(estado.eventoBotActualIndex + 1)
    for (evento in eventos) {
        when (evento) {
            is EventoBot.Iniciar    -> { ruta.clear(); ruta.add(evento.inicio) }
            is EventoBot.Avanzar    -> ruta.add(evento.hacia)
            is EventoBot.Retroceder -> if (ruta.isNotEmpty()) ruta.removeAt(ruta.lastIndex)
            is EventoBot.Finalizar  -> { /* camino finalizado, mantener */ }
        }
    }
    return ruta
}
