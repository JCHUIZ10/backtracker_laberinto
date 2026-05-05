package com.uns.backtracker

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uns.backtracker.viewmodel.MazeViewModel
import com.uns.backtracker.vista.MazeCanvas
import com.uns.backtracker.vista.MazeControls
import com.uns.backtracker.vista.MazeTheme

@Composable
fun App() {
    val modeloVista = remember { MazeViewModel() }
    val estado by modeloVista.state.collectAsState()

    MazeTheme {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val esEstrecho = maxWidth < 850.dp
            
            if (esEstrecho) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1.2f)) {
                        MazeCanvas(
                            estado = estado, 
                            onCellTap = { r, c -> modeloVista.onCeldaClickeada(com.uns.backtracker.dominio.model.Coordenada(r, c)) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    Surface(modifier = Modifier.weight(1f).fillMaxWidth(), tonalElevation = 2.dp) {
                        MazeControls(viewModel = modeloVista, modifier = Modifier.fillMaxSize())
                    }
                }
            } else {
                Row(modifier = Modifier.fillMaxSize()) {
                    Surface(modifier = Modifier.width(420.dp).fillMaxHeight(), tonalElevation = 2.dp) {
                        MazeControls(viewModel = modeloVista, modifier = Modifier.fillMaxSize())
                    }
                    Divider(
                        modifier = Modifier.fillMaxHeight().width(1.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        MazeCanvas(
                            estado = estado, 
                            onCellTap = { r, c -> modeloVista.onCeldaClickeada(com.uns.backtracker.dominio.model.Coordenada(r, c)) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}