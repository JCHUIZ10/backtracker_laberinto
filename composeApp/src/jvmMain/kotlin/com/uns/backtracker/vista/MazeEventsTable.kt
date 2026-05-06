package com.uns.backtracker.vista

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uns.backtracker.dominio.eventos.EventoLaberinto
import com.uns.backtracker.viewmodel.MazeState

@Composable
fun MazeEventsTable(estado: MazeState, modifier: Modifier = Modifier) {
    val scrollState = rememberLazyListState()
    
    LaunchedEffect(estado.eventoActualIndex) {
        if (estado.eventoActualIndex >= 0) {
            scrollState.animateScrollToItem(estado.eventoActualIndex)
        }
    }

    Column(
        modifier = modifier
            .background(BgPanelColor)
            .padding(4.dp)
    ) {
        // Cabecera de la Tabla Estilizada
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Evento de Generación",
                modifier = Modifier.weight(1.8f),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Fila",
                modifier = Modifier.weight(0.8f),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Col",
                modifier = Modifier.weight(0.8f),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(4.dp))

        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            itemsIndexed(estado.eventos.take(estado.eventoActualIndex + 1)) { index, evento ->
                val (fila, col) = evento.obtenerCoordenadas()
                val isLast = index == estado.eventoActualIndex
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (isLast) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                                    else if (index % 2 == 0) Color.White.copy(alpha = 0.02f)
                                    else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Columna de Operación con Badge personalizado
                    Box(modifier = Modifier.weight(1.8f)) {
                        evento.Badge()
                    }
                    
                    // Coordenada Fila
                    Text(
                        text = fila,
                        modifier = Modifier.weight(0.8f),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (isLast) FontWeight.Bold else FontWeight.Normal),
                        color = if (isLast) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Coordenada Columna
                    Text(
                        text = col,
                        modifier = Modifier.weight(0.8f),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (isLast) FontWeight.Bold else FontWeight.Normal),
                        color = if (isLast) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun EventoLaberinto.Badge() {
    val (label, containerColor, contentColor) = when (this) {
        is EventoLaberinto.Iniciado -> Triple("INICIADO", InicioColor.copy(alpha = 0.15f), InicioColor)
        is EventoLaberinto.Cavado -> Triple("CAVADO", MineroColor.copy(alpha = 0.15f), MineroColor)
        is EventoLaberinto.Retroceso -> Triple("RETROCESO", CuartoCentroColor.copy(alpha = 0.15f), CuartoCentroColor)
        is EventoLaberinto.CicloCreado -> Triple("CICLO", RutaBotColor.copy(alpha = 0.15f), RutaBotColor)
        is EventoLaberinto.FaseFinalizada -> Triple(nombreFase.uppercase(), RutaOptimaColor.copy(alpha = 0.15f), RutaOptimaColor)
        is EventoLaberinto.CaminoActualizado -> Triple("RUTA", MineroColor.copy(alpha = 0.15f), MineroColor)
        is EventoLaberinto.Finalizado -> Triple("FINALIZADO", RutaOptimaColor.copy(alpha = 0.20f), RutaOptimaColor)
    }
    
    Box(
        modifier = Modifier
            .background(containerColor, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            maxLines = 1
        )
    }
}

private fun EventoLaberinto.obtenerCoordenadas(): Pair<String, String> {
    return when (this) {
        is EventoLaberinto.Iniciado -> Pair(configuracion.inicio.fila.toString(), configuracion.inicio.columna.toString())
        is EventoLaberinto.Cavado -> Pair(hacia.fila.toString(), hacia.columna.toString())
        is EventoLaberinto.Retroceso -> Pair(hacia.fila.toString(), hacia.columna.toString())
        is EventoLaberinto.CicloCreado -> Pair(hacia.fila.toString(), hacia.columna.toString())
        else -> Pair("-", "-")
    }
}