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
import com.uns.backtracker.model.Direccion
import com.uns.backtracker.viewmodel.MazeState

@Composable
fun MazeCanvas(estado: MazeState, modifier: Modifier = Modifier) {
    var escala by remember { mutableStateOf(1f) }
    var desplazamiento by remember { mutableStateOf(Offset.Zero) }

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
            .graphicsLayer(
                scaleX = escala,
                scaleY = escala,
                translationX = desplazamiento.x,
                translationY = desplazamiento.y
            )
    ) {
        val laberinto = estado.animLaberinto ?: return@Canvas
        
        // Calcular tamaño de celda dinámicamente
        val anchoDisponible = size.width * 0.9f
        val altoDisponible = size.height * 0.9f
        
        if (laberinto.ancho == 0 || laberinto.alto == 0) return@Canvas
        
        val tamanoCelda = minOf(
            anchoDisponible / laberinto.ancho,
            altoDisponible / laberinto.alto
        ).coerceAtLeast(1f)
        
        // Centrar el laberinto
        val margenX = (size.width - (tamanoCelda * laberinto.ancho)) / 2f
        val margenY = (size.height - (tamanoCelda * laberinto.alto)) / 2f
        
        // Dibujar visitadas
        estado.visitadas.forEach { pos ->
            drawRect(
                color = VisitadaColor,
                topLeft = Offset(margenX + pos.x * tamanoCelda, margenY + pos.y * tamanoCelda),
                size = Size(tamanoCelda, tamanoCelda)
            )
        }

        // Dibujar Cuarto Centro
        val tamCuarto = estado.config.tamanoCuartoCentro
        if (tamCuarto > 0) {
            val centroX = laberinto.ancho / 2
            val centroY = laberinto.alto / 2
            val inicioX = centroX - tamCuarto / 2
            val inicioY = centroY - tamCuarto / 2
            
            drawRect(
                color = CuartoCentroColor.copy(alpha = 0.25f),
                topLeft = Offset(margenX + inicioX * tamanoCelda, margenY + inicioY * tamanoCelda),
                size = Size(tamCuarto * tamanoCelda, tamCuarto * tamanoCelda)
            )
        }
        
        // Dibujar Paredes
        val grosorPared = (tamanoCelda * 0.15f).coerceIn(1f, 4f)
        val centroX = laberinto.ancho / 2
        val centroY = laberinto.alto / 2
        val inicioX = centroX - tamCuarto / 2
        val inicioY = centroY - tamCuarto / 2
        val finX = inicioX + tamCuarto
        val finY = inicioY + tamCuarto

        val enCentro = { cx: Int, cy: Int -> 
            tamCuarto > 0 && cx in inicioX until finX && cy in inicioY until finY 
        }

        for(y in 0 until laberinto.alto) {
            for(x in 0 until laberinto.ancho) {
                val celda = laberinto.celdas[y][x]
                val px = margenX + x * tamanoCelda
                val py = margenY + y * tamanoCelda
                
                if (!celda.estaAbiertaHacia(Direccion.ARRIBA)) {
                    if (!(enCentro(x, y) && enCentro(x, y - 1))) {
                        drawLine(ParedColor, Offset(px, py), Offset(px + tamanoCelda, py), grosorPared, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    }
                }
                if (!celda.estaAbiertaHacia(Direccion.IZQUIERDA)) {
                    if (!(enCentro(x, y) && enCentro(x - 1, y))) {
                        drawLine(ParedColor, Offset(px, py), Offset(px, py + tamanoCelda), grosorPared, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    }
                }
                if (x == laberinto.ancho - 1 && !celda.estaAbiertaHacia(Direccion.DERECHA)) {
                    drawLine(ParedColor, Offset(px + tamanoCelda, py), Offset(px + tamanoCelda, py + tamanoCelda), grosorPared, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                }
                if (y == laberinto.alto - 1 && !celda.estaAbiertaHacia(Direccion.ABAJO)) {
                    drawLine(ParedColor, Offset(px, py + tamanoCelda), Offset(px + tamanoCelda, py + tamanoCelda), grosorPared, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                }
            }
        }
        
        // Dibujar Ruta Óptima
        estado.evaluacion?.let { eval ->
            val colorRuta = RutaOptimaColor.copy(alpha = 0.8f)
            val grosorRuta = tamanoCelda * 0.25f
            for (i in 0 until eval.rutaOptima.size - 1) {
                val actual = eval.rutaOptima[i]
                val siguiente = eval.rutaOptima[i + 1]
                
                val startX = margenX + actual.x * tamanoCelda + tamanoCelda / 2f
                val startY = margenY + actual.y * tamanoCelda + tamanoCelda / 2f
                val endX = margenX + siguiente.x * tamanoCelda + tamanoCelda / 2f
                val endY = margenY + siguiente.y * tamanoCelda + tamanoCelda / 2f
                
                drawLine(colorRuta, Offset(startX, startY), Offset(endX, endY), grosorRuta, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            }
        }

        // Dibujar Minero
        estado.mineroPos?.let { pos ->
            val px = margenX + pos.x * tamanoCelda
            val py = margenY + pos.y * tamanoCelda
            val tamMinero = tamanoCelda * 0.6f
            val margenMinero = (tamanoCelda - tamMinero) / 2f
            
            drawOval(
                color = MineroColor.copy(alpha = 0.3f),
                topLeft = Offset(px + margenMinero - 2f, py + margenMinero - 2f),
                size = Size(tamMinero + 4f, tamMinero + 4f)
            )
            drawOval(
                color = MineroColor,
                topLeft = Offset(px + margenMinero, py + margenMinero),
                size = Size(tamMinero, tamMinero)
            )
        }
    }
}
