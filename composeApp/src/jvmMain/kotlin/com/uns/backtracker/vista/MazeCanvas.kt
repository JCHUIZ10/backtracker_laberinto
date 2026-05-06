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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import com.uns.backtracker.dominio.eventos.EventoBot
import com.uns.backtracker.dominio.model.*
import com.uns.backtracker.viewmodel.MazeState

@Composable
fun MazeCanvas(estado: MazeState, onCellTap: (Int, Int) -> Unit = { _, _ -> }, modifier: Modifier = Modifier) {
    var escala by remember { mutableStateOf(1f) }
    var desplazamiento by remember { mutableStateOf(Offset.Zero) }
    var tamanoCeldaActual by remember { mutableStateOf(0f) }
    var margenXActual by remember { mutableStateOf(0f) }
    var margenYActual by remember { mutableStateOf(0f) }

    val botPainter = painterResource("bot.png")

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
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val clickRealX = (offset.x - centerX) / escala + centerX - desplazamiento.x / escala
                        val clickRealY = (offset.y - centerY) / escala + centerY - desplazamiento.y / escala
                        val col = ((clickRealX - margenXActual) / tamanoCeldaActual).toInt()
                        val fila = ((clickRealY - margenYActual) / tamanoCeldaActual).toInt()
                        val laberinto = estado.animLaberinto
                        if (laberinto != null && fila in 0 until laberinto.configuracion.filas && col in 0 until laberinto.configuracion.columnas) {
                            onCellTap(fila, col)
                        }
                    }
                }
            }
            .graphicsLayer(
                scaleX = escala,
                scaleY = escala,
                translationX = desplazamiento.x,
                translationY = desplazamiento.y
            )
    ) {
        val laberinto = estado.animLaberinto ?: return@Canvas
        val filas = laberinto.configuracion.filas
        val columnas = laberinto.configuracion.columnas
        
        val anchoDisponible = size.width * 0.95f
        val altoDisponible = size.height * 0.95f
        val tamanoCelda = minOf(anchoDisponible / columnas, altoDisponible / filas).coerceAtLeast(1f)
        val margenX = (size.width - (tamanoCelda * columnas)) / 2f
        val margenY = (size.height - (tamanoCelda * filas)) / 2f
        
        tamanoCeldaActual = tamanoCelda
        margenXActual = margenX
        margenYActual = margenY
        
        // 1. Dibujar visitadas
        estado.visitadas.forEach { pos ->
            drawRect(
                color = MineroColor.copy(alpha = 0.15f),
                topLeft = Offset(margenX + pos.columna * tamanoCelda, margenY + pos.fila * tamanoCelda),
                size = Size(tamanoCelda, tamanoCelda)
            )
        }

        // 2. Dibujar Paredes
        val grosorPared = (tamanoCelda * 0.12f).coerceIn(1f, 4f)
        for (r in 0 until filas) {
            for (c in 0 until columnas) {
                val celda = laberinto.grilla[r][c]
                val px = margenX + c * tamanoCelda
                val py = margenY + r * tamanoCelda
                if (celda.tienePared(Direccion.ARRIBA)) drawLine(ParedColor, Offset(px, py), Offset(px + tamanoCelda, py), grosorPared, cap = StrokeCap.Round)
                if (celda.tienePared(Direccion.IZQUIERDA)) drawLine(ParedColor, Offset(px, py), Offset(px, py + tamanoCelda), grosorPared, cap = StrokeCap.Round)
                if (c == columnas - 1 && celda.tienePared(Direccion.DERECHA)) drawLine(ParedColor, Offset(px + tamanoCelda, py), Offset(px + tamanoCelda, py + tamanoCelda), grosorPared, cap = StrokeCap.Round)
                if (r == filas - 1 && celda.tienePared(Direccion.ABAJO)) drawLine(ParedColor, Offset(px, py + tamanoCelda), Offset(px + tamanoCelda, py + tamanoCelda), grosorPared, cap = StrokeCap.Round)
            }
        }
        
        // 3. Dibujar S y E
        val inicio = laberinto.configuracion.inicio
        val fin = laberinto.configuracion.fin
        drawRect(InicioColor.copy(alpha = 0.4f), Offset(margenX + inicio.columna * tamanoCelda, margenY + inicio.fila * tamanoCelda), Size(tamanoCelda, tamanoCelda))
        drawRect(CuartoCentroColor.copy(alpha = 0.4f), Offset(margenX + fin.columna * tamanoCelda, margenY + fin.fila * tamanoCelda), Size(tamanoCelda, tamanoCelda))

        // 4. Ruta Óptima
        if (estado.mostrarRutaOptima && estado.caminoOptimoVisual.isNotEmpty()) {
            val colorRuta = RutaOptimaColor.copy(alpha = 0.8f)
            val grosorRuta = tamanoCelda * 0.25f
            for (i in 0 until estado.caminoOptimoVisual.size - 1) {
                val actual = estado.caminoOptimoVisual[i]
                val siguiente = estado.caminoOptimoVisual[i + 1]
                val startX = margenX + actual.columna * tamanoCelda + tamanoCelda / 2f
                val startY = margenY + actual.fila * tamanoCelda + tamanoCelda / 2f
                val endX = margenX + siguiente.columna * tamanoCelda + tamanoCelda / 2f
                val endY = margenY + siguiente.fila * tamanoCelda + tamanoCelda / 2f
                drawLine(colorRuta, Offset(startX, startY), Offset(endX, endY), grosorRuta, cap = StrokeCap.Round)
            }
        }

        // 4.5. Ruta Recorrida del Bot (Camino activo del Bot)
        if (estado.mostrarRutaBot) {
            val rutaBot = obtenerRutaActualBot(estado)
            if (rutaBot.isNotEmpty()) {
                val colorRutaBot = RutaBotColor.copy(alpha = 0.8f)
                val grosorRutaBot = tamanoCelda * 0.20f // Un poco más delgada para distinguirla
                for (i in 0 until rutaBot.size - 1) {
                    val actual = rutaBot[i]
                    val siguiente = rutaBot[i + 1]
                    val startX = margenX + actual.columna * tamanoCelda + tamanoCelda / 2f
                    val startY = margenY + actual.fila * tamanoCelda + tamanoCelda / 2f
                    val endX = margenX + siguiente.columna * tamanoCelda + tamanoCelda / 2f
                    val endY = margenY + siguiente.fila * tamanoCelda + tamanoCelda / 2f
                    drawLine(colorRutaBot, Offset(startX, startY), Offset(endX, endY), grosorRutaBot, cap = StrokeCap.Round)
                }
            }
        }

        // 5. Minero
        estado.mineroPos?.let { pos ->
            val px = margenX + pos.columna * tamanoCelda
            val py = margenY + pos.fila * tamanoCelda
            val tamMinero = tamanoCelda * 0.6f
            val margenMinero = (tamanoCelda - tamMinero) / 2f
            drawOval(MineroColor.copy(alpha = 0.3f), Offset(px + margenMinero - 2f, py + margenMinero - 2f), Size(tamMinero + 4f, tamMinero + 4f))
            drawOval(MineroColor, Offset(px + margenMinero, py + margenMinero), Size(tamMinero, tamMinero))
        }

        // 6. Bot Explorador
        estado.botPosActual?.let { pos ->
            val px = margenX + pos.columna * tamanoCelda
            val py = margenY + pos.fila * tamanoCelda
            val tamBot = tamanoCelda * 0.8f
            val margenBot = (tamanoCelda - tamBot) / 2f
            
            // Dibujar aura de color naranja neon
            drawOval(
                color = androidx.compose.ui.graphics.Color(0xFFFFA726).copy(alpha = 0.4f),
                topLeft = Offset(px + margenBot - 2f, py + margenBot - 2f),
                size = Size(tamBot + 4f, tamBot + 4f)
            )

            // Dibujar bot.png
            val startX = px + margenBot
            val startY = py + margenBot
            // Usamos drawIntoCanvas para dibujar con el Painter
            drawContext.canvas.save()
            drawContext.canvas.translate(startX, startY)
            with(botPainter) {
                draw(size = Size(tamBot, tamBot))
            }
            drawContext.canvas.restore()
        }
    }
}

private fun obtenerRutaActualBot(estado: MazeState): List<Coordenada> {
    val ruta = mutableListOf<Coordenada>()
    val eventos = estado.eventosBot.take(estado.eventoBotActualIndex + 1)
    for (evento in eventos) {
        when (evento) {
            is EventoBot.Iniciar -> {
                ruta.clear()
                ruta.add(evento.inicio)
            }
            is EventoBot.Avanzar -> {
                ruta.add(evento.hacia)
            }
            is EventoBot.Retroceder -> {
                if (ruta.isNotEmpty()) {
                    ruta.removeAt(ruta.lastIndex)
                }
            }
            is EventoBot.Finalizar -> {
                // Mantiene el camino finalizado
            }
        }
    }
    return ruta
}
