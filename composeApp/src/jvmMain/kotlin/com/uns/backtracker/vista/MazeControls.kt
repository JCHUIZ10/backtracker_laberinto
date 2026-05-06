package com.uns.backtracker.vista

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.uns.backtracker.dominio.model.*
import com.uns.backtracker.viewmodel.MazeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MazeControls(viewModel: MazeViewModel, modifier: Modifier = Modifier) {
    val estado by viewModel.state.collectAsState()
    
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SeccionExpandible(titulo = "Configuración", icono = Icons.Default.Settings, inicialmenteExpandido = true) {
            var anchoStr by remember { mutableStateOf(estado.config.columnas.toString()) }
            var altoStr by remember { mutableStateOf(estado.config.filas.toString()) }
            var semillaStr by remember { mutableStateOf(estado.config.semillaValue.toString()) }
            var sFilaStr by remember { mutableStateOf(estado.config.inicio.fila.toString()) }
            var sColStr by remember { mutableStateOf(estado.config.inicio.columna.toString()) }
            var eFilaStr by remember { mutableStateOf(estado.config.fin.fila.toString()) }
            var eColStr by remember { mutableStateOf(estado.config.fin.columna.toString()) }
            var dificultad by remember { mutableStateOf(estado.config.dificultad) }
            var expandedDificultad by remember { mutableStateOf(false) }
            var mensajeError by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(estado.config) {
                sFilaStr = estado.config.inicio.fila.toString()
                sColStr = estado.config.inicio.columna.toString()
                eFilaStr = estado.config.fin.fila.toString()
                eColStr = estado.config.fin.columna.toString()
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = anchoStr,
                        onValueChange = { if (it.all { c -> c.isDigit() }) anchoStr = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Cols") }
                    )
                    OutlinedTextField(
                        value = altoStr,
                        onValueChange = { if (it.all { c -> c.isDigit() }) altoStr = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Filas") }
                    )
                }
                OutlinedTextField(
                    value = semillaStr,
                    onValueChange = { if (it.all { c -> c.isDigit() }) semillaStr = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Semilla") },
                    trailingIcon = {
                        IconButton(onClick = { semillaStr = System.currentTimeMillis().toString() }) {
                            Icon(Icons.Default.Casino, contentDescription = null)
                        }
                    }
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary)
                    Text("Inicio (S)", Modifier.weight(1f))
                    OutlinedTextField(
                        value = sFilaStr,
                        onValueChange = { sFilaStr = it },
                        modifier = Modifier.width(70.dp),
                        label = { Text("F") }
                    )
                    OutlinedTextField(
                        value = sColStr,
                        onValueChange = { sColStr = it },
                        modifier = Modifier.width(70.dp),
                        label = { Text("C") }
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Flag, null, tint = MaterialTheme.colorScheme.secondary)
                    Text("Fin (E)", Modifier.weight(1f))
                    OutlinedTextField(
                        value = eFilaStr,
                        onValueChange = { eFilaStr = it },
                        modifier = Modifier.width(70.dp),
                        label = { Text("F") }
                    )
                    OutlinedTextField(
                        value = eColStr,
                        onValueChange = { eColStr = it },
                        modifier = Modifier.width(70.dp),
                        label = { Text("C") }
                    )
                }
                Box(Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = dificultad.name.lowercase().replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        label = { Text("Dificultad") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { expandedDificultad = true }) {
                                Icon(Icons.Default.ArrowDropDown, null)
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expandedDificultad,
                        onDismissRequest = { expandedDificultad = false },
                        modifier = Modifier.width(200.dp)
                    ) {
                        Dificultad.entries.forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    dificultad = level
                                    expandedDificultad = false
                                }
                            )
                        }
                    }
                }
                Button(
                    onClick = {
                        val w = anchoStr.toIntOrNull()
                        val h = altoStr.toIntOrNull()
                        val s = semillaStr.toLongOrNull() ?: 0L
                        val sf = sFilaStr.toIntOrNull()
                        val sc = sColStr.toIntOrNull()
                        val ef = eFilaStr.toIntOrNull()
                        val ec = eColStr.toIntOrNull()
                        if (w != null && h != null && sf != null && sc != null && ef != null && ec != null) {
                            if (sf in 0 until h && sc in 0 until w && ef in 0 until h && ec in 0 until w) {
                                mensajeError = null
                                viewModel.updateConfigManual(w, h, s, dificultad, Coordenada(sf, sc), Coordenada(ef, ec))
                                viewModel.generar()
                            } else mensajeError = "Coordenadas fuera de rango."
                        } else mensajeError = "Datos inválidos."
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Generar")
                }
                mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }

        Spacer(Modifier.height(8.dp))
        SeccionExpandible("Reproducción", Icons.Default.PlayArrow, true) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Evento: ${estado.eventoActualIndex + 1} / ${estado.eventos.size}", color = MaterialTheme.colorScheme.primary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.togglePlayPause() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(if (estado.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null)
                        Spacer(Modifier.width(4.dp))
                        Text(if (estado.isPlaying) "Pausar" else "Play")
                    }
                    OutlinedButton(
                        onClick = { viewModel.avanzarPaso() },
                        modifier = Modifier.weight(1f),
                        enabled = !estado.isPlaying
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                        Spacer(Modifier.width(4.dp))
                        Text("Paso")
                    }
                }
                Slider(
                    value = estado.velocidadMs.toFloat(),
                    onValueChange = { viewModel.updateSpeed(it.toLong()) },
                    valueRange = 5f..500f
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        SeccionExpandible("Log de Operaciones", Icons.AutoMirrored.Filled.List, false) {
            MazeEventsTable(estado = estado, modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp))
        }

        Spacer(Modifier.height(8.dp))
        SeccionExpandible("Bot Explorador", Icons.Default.Android, false) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                val botImage = painterResource("bot.png")
                
                Image(
                    painter = botImage,
                    contentDescription = "Bot Logo",
                    modifier = Modifier
                        .size(90.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 8.dp)
                )
                
                Text(
                    text = "Evento Bot: ${estado.eventoBotActualIndex + 1} / ${estado.eventosBot.size}", 
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.toggleBotPlayPause() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(if (estado.isBotPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null)
                        Spacer(Modifier.width(4.dp))
                        Text(if (estado.isBotPlaying) "Pausar" else "Play")
                    }
                    OutlinedButton(
                        onClick = { viewModel.avanzarPasoBot() },
                        modifier = Modifier.weight(1f),
                        enabled = !estado.isBotPlaying
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                        Spacer(Modifier.width(4.dp))
                        Text("Paso")
                    }
                }
                
                Spacer(Modifier.height(4.dp))
                Text("Historial de Exploración", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                BotEventsTable(estado = estado, modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp))
            }
        }

        Spacer(Modifier.height(8.dp))
        SeccionExpandible("Métricas", Icons.Default.Analytics, false) {
            val lab = estado.laberintoFinal
            if (lab != null) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    MetricaConInfo("Camino Óptimo (L*)", "${lab.metricas.longitudCaminoOptimo}", "Longitud mínima real.")
                    MetricaConInfo("Optimalidad (ρ)", "%.2f".format(lab.metricas.ratioOptimalidad), "Eficiencia de la ruta.")
                    MetricaConInfo("Ramificación (b)", "%.2f".format(lab.metricas.factorRamificacion), "Densidad de bifurcaciones.")
                }
            } else {
                Text("Sin datos.", style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(Modifier.height(8.dp))
        SeccionExpandible("Mostrar Caminos", Icons.Default.Visibility, false) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = estado.mostrarRutaOptima,
                        onCheckedChange = { viewModel.toggleMostrarRutaOptima() }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Ruta Óptima", style = MaterialTheme.typography.bodyMedium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = estado.mostrarRutaBot,
                        onCheckedChange = { viewModel.toggleMostrarRutaBot() }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Ruta Recorrida del Bot", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MetricaConInfo(etiqueta: String, valor: String, explicacion: String) {
    TooltipArea(
        tooltip = {
            Surface(
                modifier = Modifier.shadow(8.dp),
                color = MaterialTheme.colorScheme.inverseSurface,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = explicacion,
                    modifier = Modifier.padding(12.dp).widthIn(max = 280.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Text(etiqueta, Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
            Text(valor, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Default.HelpOutline, null, Modifier.size(16.dp))
        }
    }
}
