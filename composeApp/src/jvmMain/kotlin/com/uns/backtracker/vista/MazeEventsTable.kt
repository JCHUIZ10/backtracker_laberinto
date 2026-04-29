package com.uns.backtracker.vista

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uns.backtracker.model.EventoGeneracion
import com.uns.backtracker.viewmodel.MazeState

@Composable
fun MazeEventsTable(estado: MazeState, modifier: Modifier = Modifier) {
    val estadoLista = rememberLazyListState()

    // Autoscroll al evento actual
    LaunchedEffect(estado.eventoActual) {
        if (estado.eventoActual > 0 && estado.eventoActual <= estado.eventosHistory.size) {
            estadoLista.animateScrollToItem((estado.eventoActual - 1).coerceAtLeast(0))
        }
    }

    Column(modifier = modifier.padding(vertical = 4.dp)) {
        // Cabecera
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CeldaCabecera("N", Modifier.weight(0.15f))
            CeldaCabecera("Evento", Modifier.weight(0.45f))
            CeldaCabecera("X", Modifier.weight(0.2f))
            CeldaCabecera("Y", Modifier.weight(0.2f))
        }

        Divider(thickness = 2.dp, color = MaterialTheme.colorScheme.outline)

        // Cuerpo con altura fija para el acordeón
        LazyColumn(
            state = estadoLista,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp) // Altura fija para que el acordeón no sea infinito
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            itemsIndexed(estado.eventosHistory) { indice, evento ->
                val esActual = indice == estado.eventoActual - 1
                val (tipo, x, y) = evento.aDatosFila()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (esActual) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                            else if (indice % 2 == 0) Color.Transparent
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CeldaTabla((indice + 1).toString(), Modifier.weight(0.15f), esActual)
                    
                    val colorTexto = when (evento) {
                        is EventoGeneracion.Iniciado -> MaterialTheme.colorScheme.tertiary
                        is EventoGeneracion.Cavado -> MaterialTheme.colorScheme.primary
                        is EventoGeneracion.Retrocedido -> MaterialTheme.colorScheme.error
                        is EventoGeneracion.Finalizado -> MaterialTheme.colorScheme.secondary
                    }
                    
                    CeldaTabla(tipo, Modifier.weight(0.45f), esActual, colorTexto)
                    CeldaTabla(x, Modifier.weight(0.2f), esActual)
                    CeldaTabla(y, Modifier.weight(0.2f), esActual)
                }
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
private fun CeldaCabecera(texto: String, modifier: Modifier) {
    Text(
        text = texto,
        modifier = modifier,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

@Composable
private fun CeldaTabla(
    texto: String, 
    modifier: Modifier, 
    esResaltado: Boolean = false,
    colorTexto: Color? = null
) {
    Text(
        text = texto,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = if (esResaltado) FontWeight.ExtraBold else FontWeight.Normal,
        color = colorTexto ?: (if (esResaltado) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface),
        fontSize = 11.sp
    )
}

private fun EventoGeneracion.aDatosFila(): Triple<String, String, String> {
    return when (this) {
        is EventoGeneracion.Iniciado -> Triple("INICIO", posicionInicial.x.toString(), posicionInicial.y.toString())
        is EventoGeneracion.Cavado -> Triple("CAVAR", hasta.x.toString(), hasta.y.toString())
        is EventoGeneracion.Retrocedido -> Triple("RETROCEDE", hasta.x.toString(), hasta.y.toString())
        is EventoGeneracion.Finalizado -> Triple("FINAL", "-", "-")
    }
}