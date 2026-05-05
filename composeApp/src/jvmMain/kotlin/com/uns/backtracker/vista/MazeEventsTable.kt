package com.uns.backtracker.vista

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Column(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)).padding(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer).padding(8.dp)) {
            Text(text = "Operación", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
            Text(text = "Fila", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
            Text(text = "Col", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
        }

        LazyColumn(state = scrollState, modifier = Modifier.fillMaxWidth()) {
            items(estado.eventos.take(estado.eventoActualIndex + 1)) { evento ->
                val (op, fila, col) = evento.aDatosFila()
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = op, 
                        modifier = Modifier.weight(1.5f), 
                        style = MaterialTheme.typography.bodySmall, 
                        color = if (op.startsWith("FASE")) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                    )
                    Text(text = fila, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                    Text(text = col, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                }
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }
}

private fun EventoLaberinto.aDatosFila(): Triple<String, String, String> {
    return when (this) {
        is EventoLaberinto.Iniciado -> Triple("INICIADO", configuracion.inicio.fila.toString(), configuracion.inicio.columna.toString())
        is EventoLaberinto.Cavado -> Triple("CAVADO", hacia.fila.toString(), hacia.columna.toString())
        is EventoLaberinto.Retroceso -> Triple("RETROCESO", hacia.fila.toString(), hacia.columna.toString())
        is EventoLaberinto.CicloCreado -> Triple("CICLO", hacia.fila.toString(), hacia.columna.toString())
        is EventoLaberinto.FaseFinalizada -> Triple("FASE: ${nombreFase}", "-", "-")
        is EventoLaberinto.CaminoActualizado -> Triple("RUTA", "-", "-")
        is EventoLaberinto.Finalizado -> Triple("FINALIZADO", "-", "-")
    }
}