package com.uns.backtracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uns.backtracker.dominio.model.Coordenada
import com.uns.backtracker.viewmodel.MazeViewModel
import com.uns.backtracker.vista.*

@Composable
fun App() {
    val modeloVista = remember { MazeViewModel() }
    val estado by modeloVista.state.collectAsState()

    MazeTheme {
        BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BgColor)) {
            val esEstrecho = maxWidth < 860.dp

            if (esEstrecho) {
                // Layout apilado (pantallas estrechas)
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1.3f)) {
                        MazeCanvas(
                            estado = estado,
                            onCellTap = { r, c -> modeloVista.onCeldaClickeada(Coordenada(r, c)) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                    Surface(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        MazeControls(viewModel = modeloVista, modifier = Modifier.fillMaxSize())
                    }
                }
            } else {
                // Layout horizontal (pantallas anchas)
                Row(modifier = Modifier.fillMaxSize()) {
                    // Panel lateral izquierdo
                    Surface(
                        modifier = Modifier.width(430.dp).fillMaxHeight(),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 4.dp
                    ) {
                        MazeControls(viewModel = modeloVista, modifier = Modifier.fillMaxSize())
                    }
                    // Separador
                    Divider(
                        modifier = Modifier.fillMaxHeight().width(1.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    // Canvas del laberinto
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        MazeCanvas(
                            estado = estado,
                            onCellTap = { r, c -> modeloVista.onCeldaClickeada(Coordenada(r, c)) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}