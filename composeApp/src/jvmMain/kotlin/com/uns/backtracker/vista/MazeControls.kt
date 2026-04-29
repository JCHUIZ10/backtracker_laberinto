package com.uns.backtracker.vista

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.text.style.TextOverflow
import com.uns.backtracker.viewmodel.MazeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MazeControls(viewModel: MazeViewModel, modifier: Modifier = Modifier) {
    val estado by viewModel.state.collectAsState()
    
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // --- SECCIÓN: CONFIGURACIÓN ---
        SeccionExpandible(
            titulo = "Configuración",
            icono = Icons.Default.Settings,
            inicialmenteExpandido = true
        ) {
            var anchoStr by remember { mutableStateOf(estado.config.ancho.toString()) }
            var altoStr by remember { mutableStateOf(estado.config.alto.toString()) }
            var semillaStr by remember { mutableStateOf(estado.config.semilla.toString()) }
            var cuartoStr by remember { mutableStateOf(estado.config.tamanoCuartoCentro.toString()) }
            var mensajeError by remember { mutableStateOf<String?>(null) }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = anchoStr, 
                        onValueChange = { if (it.all { c -> c.isDigit() }) anchoStr = it }, 
                        label = { Text("Ancho") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = altoStr, 
                        onValueChange = { if (it.all { c -> c.isDigit() }) altoStr = it }, 
                        label = { Text("Alto") },
                        singleLine = true
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = semillaStr, 
                        onValueChange = { if (it.all { c -> c.isDigit() }) semillaStr = it }, 
                        label = { Text("Semilla") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = cuartoStr, 
                        onValueChange = { if (it.all { c -> c.isDigit() }) cuartoStr = it }, 
                        label = { Text("Centro") },
                        singleLine = true
                    )
                }
                
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { 
                        val w = anchoStr.toIntOrNull()
                        val h = altoStr.toIntOrNull()
                        val s = semillaStr.toLongOrNull() ?: System.currentTimeMillis()
                        val c = cuartoStr.toIntOrNull()

                        when {
                            w == null || h == null || c == null -> {
                                mensajeError = "Ingresa números válidos."
                            }
                            w <= 0 || h <= 0 -> {
                                mensajeError = "Ancho y alto deben ser positivos."
                            }
                            w > 100 || h > 100 -> {
                                mensajeError = "Máximo 100x100 para rendimiento."
                            }
                            c < 1 -> {
                                mensajeError = "Centro mínimo 1."
                            }
                            c >= w || c >= h -> {
                                mensajeError = "Centro muy grande."
                            }
                            else -> {
                                mensajeError = null
                                viewModel.updateConfig(w, h, s, c)
                                viewModel.generar()
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generar")
                }

                mensajeError?.let { error ->
                    Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // --- SECCIÓN: REPRODUCCIÓN ---
        SeccionExpandible(
            titulo = "Reproducción",
            icono = Icons.Default.PlayArrow,
            inicialmenteExpandido = true
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Paso: ${estado.eventoActual} / ${estado.totalEventos}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(modifier = Modifier.weight(1f), onClick = { viewModel.togglePlayPause() }) {
                        Icon(if (estado.isPlaying) Icons.Default.Close else Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (estado.isPlaying) "Pausar" else "Play")
                    }
                    Button(modifier = Modifier.weight(1f), onClick = { viewModel.avanzarPaso() }, enabled = !estado.isPlaying) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Paso")
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                Text("Velocidad: ${estado.velocidadMs}ms", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = estado.velocidadMs.toFloat(),
                    onValueChange = { viewModel.updateSpeed(it.toLong()) },
                    valueRange = 1f..100f
                )
            }
        }

        // --- SECCIÓN: HISTORIAL DE EVENTOS ---
        SeccionExpandible(
            titulo = "Historial de Eventos",
            icono = Icons.AutoMirrored.Filled.List,
            inicialmenteExpandido = false
        ) {
            MazeEventsTable(estado = estado, modifier = Modifier.fillMaxWidth())
        }

        // --- SECCIÓN: EVALUACIÓN ---
        SeccionExpandible(
            titulo = "Evaluación / Ruta Crítica",
            icono = Icons.Default.Info,
            inicialmenteExpandido = false
        ) {
            val eval = estado.evaluacion
            if (eval != null) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ListItem(
                        headlineText = { Text("Dificultad: ${eval.nivelDificultad()}") },
                        supportingText = { Text("Valor: ${"%.2f".format(eval.dificultad)}") },
                        leadingContent = { Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) }
                    )
                    MetricaConInfo("Longitud Óptima", "${eval.longitudSolucion}", "Número de movimientos necesarios para resolver el laberinto. A mayor número, más largo es el recorrido hacia el centro.")
                    MetricaConInfo("Callejones", "${eval.callejones}", "Número de celdas con una sola salida (caminos sin salida). Engañan al usuario haciéndolo retroceder.")
                    MetricaConInfo("Bifurcaciones", "${eval.bifurcaciones}", "Número de celdas con 3 o más caminos. Representan puntos de decisión en el laberinto.")
                    MetricaConInfo("Densidad Intersec.", "%.2f".format(eval.densidadIntersecciones), "Proporción de bifurcaciones respecto al total de celdas. Mide qué tan enredado es el laberinto globalmente.")
                    MetricaConInfo("Pasillo más largo", "${eval.pasilloMasLargo}", "La ruta recta más larga sin opciones de desvío.")
                }
            } else {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.evaluarLaberinto() },
                    enabled = !estado.isPlaying && estado.eventoActual >= estado.totalEventos && estado.totalEventos > 0
                ) {
                    Text("Calcular Ruta Óptima")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MetricaConInfo(etiqueta: String, valor: String, explicacion: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
    ) {
        Text(
            text = "$etiqueta: $valor", 
            style = MaterialTheme.typography.bodySmall, 
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        TooltipArea(
            tooltip = {
                Surface(
                    modifier = Modifier.shadow(8.dp),
                    color = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = explicacion,
                        modifier = Modifier.padding(12.dp).widthIn(max = 280.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            },
            delayMillis = 200
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier
                    .size(22.dp)
                    .padding(2.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
