package com.uns.backtracker.vista

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SeccionExpandible(
    titulo: String,
    icono: ImageVector,
    inicialmenteExpandido: Boolean = false,
    contenido: @Composable () -> Unit
) {
    var expandido by remember { mutableStateOf(inicialmenteExpandido) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandido = !expandido }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Text(titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Icon(if (expandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
            }
            
            AnimatedVisibility(visible = expandido) {
                Box(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
                    contenido()
                }
            }
        }
    }
}
