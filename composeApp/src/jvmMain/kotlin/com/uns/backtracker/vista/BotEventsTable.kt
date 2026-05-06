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
import com.uns.backtracker.dominio.eventos.EventoBot
import com.uns.backtracker.viewmodel.MazeState

@Composable
fun BotEventsTable(estado: MazeState, modifier: Modifier = Modifier) {
    val scrollState = rememberLazyListState()
    
    LaunchedEffect(estado.eventoBotActualIndex) {
        if (estado.eventoBotActualIndex >= 0) {
            scrollState.animateScrollToItem(estado.eventoBotActualIndex)
        }
    }

    Column(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)).padding(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer).padding(8.dp)) {
            Text(text = "Acción Bot", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
            Text(text = "Fila", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
            Text(text = "Col", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
        }

        LazyColumn(state = scrollState, modifier = Modifier.fillMaxWidth()) {
            items(estado.eventosBot.take(estado.eventoBotActualIndex + 1)) { evento ->
                val (op, fila, col) = evento.aDatosFila()
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = op, 
                        modifier = Modifier.weight(1.5f), 
                        style = MaterialTheme.typography.bodySmall, 
                        color = when {
                            op == "RETROCEDE" -> MaterialTheme.colorScheme.error
                            op.startsWith("FINALIZA") || op == "INICIA" -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Text(text = fila, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                    Text(text = col, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                }
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }
}

private fun EventoBot.aDatosFila(): Triple<String, String, String> {
    return when (this) {
        is EventoBot.Iniciar -> Triple("INICIA", inicio.fila.toString(), inicio.columna.toString())
        is EventoBot.Avanzar -> Triple("AVANZA", hacia.fila.toString(), hacia.columna.toString())
        is EventoBot.Retroceder -> Triple("RETROCEDE", hacia.fila.toString(), hacia.columna.toString())
        is EventoBot.Finalizar -> Triple(if (exito) "FINALIZA (EXITO)" else "FINALIZA (FALLO)", fin.fila.toString(), fin.columna.toString())
    }
}
